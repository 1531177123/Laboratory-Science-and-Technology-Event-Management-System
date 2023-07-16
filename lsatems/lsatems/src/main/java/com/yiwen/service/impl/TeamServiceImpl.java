package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.common.Code;
import com.yiwen.common.Constants;
import com.yiwen.dao.TeamDao;
import com.yiwen.domain.*;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.*;
import com.yiwen.utils.CommonUtils;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 实验室展示信息表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamDao, Team> implements ITeamService {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserTeamService userTeamService;

    @Autowired
    private IPlayService playService;

    @Autowired
    private IFileService fileService;

    @Autowired
    private IUserLabService userLabService;

    @Autowired
    private IMessageService messageService;

    @Override
    public boolean saveTeam(Team team)
    {
        String name = team.getName();
        String code = team.getCode();
        if (StrUtil.isBlank(name) || StrUtil.isBlank(code))
        {
            throw new BusinessException(Code.SAVE_ERR, "名称或者组队代码不能为空！");
        }

        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getCode, code).or().eq(Team::getName, name);
        Team sTeam = getOne(queryWrapper);
        if (sTeam != null)
        {
            throw new BusinessException(Code.SAVE_ERR, "名称或组队编码重复！");
        }

        team.setCreateTime(DateTimeUtil.getSysTime());
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        team.setCreatorId(currentUserLogin.getId());
        team.setCreator(userService.getCurrentUserDetails(currentUserLogin).getName());
        team.setStatus(Constants.STATUS_TEAM_CONVENE);
        boolean saveFlag = save(team);
        //创建相应关系
        UserTeam userTeam = new UserTeam();
        userTeam.setRole(Constants.TEAM_ROLE_CREATOR);
        userTeam.setCreateTime(DateTimeUtil.getSysTime());
        userTeam.setStatus(Constants.CHECK_PASS);
        userTeam.setTeamId(team.getId());
        userTeam.setUserId(currentUserLogin.getId());
        boolean saveRelationFlag = userTeamService.save(userTeam);
        return saveFlag && saveRelationFlag;
    }

    @Override
    public boolean updateTeamById(Team team)
    {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(team.getCode()), Team::getCode, team.getCode()).
                or().eq(StrUtil.isNotBlank(team.getName()), Team::getName, team.getName());
        Team oneTeam = getOne(queryWrapper);
        if (!oneTeam.getId().equals(team.getId()))
        {
            throw new BusinessException(Code.SAVE_ERR, "名称或组队编码重复！");
        }
        team.setEditTime(DateTimeUtil.getSysTime());
        return updateById(team);
    }

    @Override
    public IPage<Team> selectPage(long currentPage, long pageSize, String name, String code, String creator)
    {
        LambdaQueryWrapper<Team> queryWrapper = selectTeamParameters(name, code, creator);
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        if (!Constants.ROLE_ADMIN.equals(currentUserLogin.getRoleType()))
        {
            handleTeamsQueryByUserId(queryWrapper, currentUserLogin.getId());
        }
        Page<Team> teamPage = teamDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        List<Team> records = handleTeamOutMix(teamPage.getRecords());
        teamPage.setRecords(records);
        return teamPage;
    }

    private List<Team> handleTeamOutMix(List<Team> teams)
    {
        List<UserTeam> userTeams = userTeamService.list().stream().filter(v -> Constants.CHECK_PASS.equals(v.getStatus())).collect(Collectors.toList());

        for (Team team : teams) {
            int personNumber = 0;
            Set<String> userIds = new HashSet<>();
            for (UserTeam userTeam : userTeams) {
                if (userTeam.getTeamId().equals(team.getId()) && !userIds.contains(userTeam.getUserId()))
                {
                    personNumber++;
                    userIds.add(userTeam.getUserId());
                }
            }
            team.setPersonNumber(String.valueOf(personNumber));
            if (team.getLimitNumber().equals(team.getPersonNumber()) && Constants.STATUS_TEAM_CONVENE.equals(team.getStatus()))
            {
                team.setStatus(Constants.STATUS_TEAM_PLAYING);
                teamDao.updateById(team);
            }
            team.setPlayName(playService.getById(team.getPlayId()).getName());
        }
        return teams;
    }

    private void handleTeamsQueryByUserId(LambdaQueryWrapper<Team> queryWrapper, String userId) {
        List<String> teamIds = userTeamService.getByUserId(userId).stream().filter(v -> Constants.CHECK_PASS.equals(v.getStatus()))
                .map(UserTeam::getTeamId).collect(Collectors.toList());
        queryWrapper.in(!teamIds.isEmpty(), Team::getId, teamIds);
    }

    @Override
    public Team getTeamById(String id)
    {
        List<Team> teams = new ArrayList<>();
        teams.add(getById(id));
        handleTeamOutMix(teams);
        Team resTeam = teams.get(0);
        handleTeamPartners(resTeam);
        return resTeam;
    }

    /***
     * 处理组队的partners
     * @param team 组队
     * @return 结果
     */
    private void handleTeamPartners(Team team)
    {

        List<String> partnerNames = new ArrayList<>();
        List<String> partnerIds = new ArrayList<>();
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId, team.getId());
        queryWrapper.eq(UserTeam::getStatus, Constants.CHECK_PASS);
        List<String> userIds = userTeamService.list(queryWrapper).stream().map(UserTeam::getUserId).collect(Collectors.toList());

        for (String userId : userIds)
        {
            UserLogin loginById = userService.getLoginById(userId);
            partnerIds.add(userId);
            partnerNames.add(userService.getCurrentUserDetails(loginById).getName());
        }
        team.setPartnerNames(partnerNames);
        team.setPartnerIds(partnerIds);
    }

    @Override
    public List<Team> getAll(String userId, String name, String code)
    {
        List<Team> teams = list(getTeamsQueryByUserId(userId));
        return handleTeamOutMix(teams);
    }

    @Override
    public IPage<Team> selectPlayPage(long currentPage, long pageSize, String name, String code, String creator, String playId)
    {

        LambdaQueryWrapper<Team> queryWrapper = selectTeamParameters(name, code, creator);
        queryWrapper.eq(StrUtil.isNotBlank(playId), Team::getPlayId, playId);
        Page<Team> teamPage = teamDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        List<Team> records = handleTeamOutMix(teamPage.getRecords());
        teamPage.setRecords(records);
        return teamPage;
    }

    @Override
    public boolean certificationScore(MultipartFile file, String playId, String teamId, String score) throws Exception
    {
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        Files saveFiles = fileService.uploadRes(file, currentUserLogin.getId());
        Team team = getById(teamId);
        team.setStatus(Constants.STATUS_TEAM_SCORE_AUDITING);
        boolean updateFlag = updateById(team);
        Play play = playService.getById(playId);
        String messageContent = "队伍[" + team.getName() + "]认证成绩:" + score;
        List<String> labAdminIds = userLabService.getLabAdminIds(play.getLabId());
        Message message = CommonUtils.assembleMessage(Constants.MEG_TYPE_TEAM_CERTIFICATION_SCORE, Constants.CHECK_APPLYING, teamId, team.getCreatorId()
                , team.getCreator(), messageContent, saveFiles.getUrl(), labAdminIds.toString());
        boolean saveMsgFlag = messageService.saveMessage(message);
        if (updateFlag && saveMsgFlag)
        {
            MessageUtils.sendMessageToUsers(Constants.MESSAGE_TYPE_WARNING, "组队成绩认证", messageContent, Constants.FRONT__PATH_MESSAGE, labAdminIds);
        }

        return updateFlag && saveMsgFlag;
    }

    @Override
    public List<Team> getByPlayId(String playId)
    {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(playId), Team::getPlayId, playId);
        queryWrapper.orderByDesc(Team::getCreateTime);
        return handleTeamOutMix(list(queryWrapper));
    }

    private  LambdaQueryWrapper<Team> selectTeamParameters(String name, String code, String creator) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Team::getName, name);
        queryWrapper.eq(StrUtil.isNotBlank(code), Team::getCode, code);
        queryWrapper.like(StrUtil.isNotBlank(creator), Team::getCreator, creator);
        queryWrapper.orderByDesc(Team::getCreateTime);
        return queryWrapper;
    }

    private LambdaQueryWrapper<Team> getTeamsQueryByUserId(String userId)
    {
        List<String> teamIds = userTeamService.getByUserId(userId).stream().filter(v -> Constants.CHECK_PASS.equals(v.getStatus()))
                .map(UserTeam::getTeamId).collect(Collectors.toList());
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Team::getId, teamIds);
        return queryWrapper;
    }
}
