package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.common.Constants;
import com.yiwen.dao.UserTeamDao;
import com.yiwen.domain.*;
import com.yiwen.service.IMessageService;
import com.yiwen.service.ITeamService;
import com.yiwen.service.IUserService;
import com.yiwen.service.IUserTeamService;
import com.yiwen.utils.CommonUtils;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class UserTeamServiceImpl extends ServiceImpl<UserTeamDao, UserTeam> implements IUserTeamService
{

    @Autowired
    private UserTeamDao userTeamDao;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITeamService teamService;

    @Override
    public boolean saveById(String userId, String teamId, String content)
    {
        UserTeam userTeam = new UserTeam();
        userTeam.setCreateTime(DateTimeUtil.getSysTime());
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setRole(Constants.TEAM_ROLE_COMMON);
        userTeam.setStatus(Constants.CHECK_APPLYING);
        boolean saveRelationFlag = save(userTeam);
        String creator = userService.getCurrentUserDetails(userService.getCurrentUserLogin()).getName();
        Team team = teamService.getById(teamId);
        String messageContent = CommonUtils.assembleMessageContent(creator, team.getName(), content);
        List<String> adminIds = getTeamAdminIds(teamId);
        Message message = CommonUtils.assembleMessage(Constants.MEG_TYPE_TEAM_APPLY, Constants.CHECK_APPLYING, userTeam.getId()
                , userId, creator, messageContent, null, adminIds.toString());
        boolean saveMsgFlag = messageService.saveMessage(message);
        if (saveRelationFlag && saveMsgFlag)
        {
            MessageUtils.sendMessageToUsers(Constants.MESSAGE_TYPE_WARNING, "组队申请", messageContent, Constants.FRONT__PATH_MESSAGE, adminIds);
        }
        return saveRelationFlag && saveMsgFlag;
    }

    private List<String> getTeamAdminIds(String teamId)
    {
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(teamId), UserTeam::getTeamId, teamId);
        queryWrapper.ne(UserTeam::getRole, Constants.TEAM_ROLE_COMMON);
        return list(queryWrapper).stream().map(UserTeam::getUserId).collect(Collectors.toList());
    }
    @Override
    public boolean updateStatus(UserTeam userTeam)
    {
        boolean updateRelationFlag = updateById(userTeam);
        String teamId = userTeam.getTeamId();
        Team team = teamService.getById(teamId);
        team.setEditTime(DateTimeUtil.getSysTime());
        boolean updateTeamFlag = teamService.updateById(team);
        String type = Constants.CHECK_PASS.equals(userTeam.getStatus()) ? Constants.MESSAGE_TYPE_SUCCESS : Constants.MESSAGE_TYPE_ERROR;
        String message = Constants.CHECK_PASS.equals(userTeam.getStatus()) ? "申请通过" : "申请失败";
        MessageUtils.sendMessageToUser(type, "组队申请", team.getName() + message, null, userTeam.getUserId());
        return updateRelationFlag && updateTeamFlag;
    }

    @Override
    public boolean quitTeam(String userId, String teamId)
    {
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getUserId, userId);
        queryWrapper.eq(UserTeam::getTeamId, teamId);
        return remove(queryWrapper);
    }

    @Override
    public List<UserTeam> getByUserId(String userId)
    {
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(userId), UserTeam::getUserId, userId);
        return list(queryWrapper);
    }

    @Override
    public List<UserTeam> getByTeamId(String teamId)
    {
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(teamId), UserTeam::getTeamId, teamId);
        return list(queryWrapper);
    }
}
