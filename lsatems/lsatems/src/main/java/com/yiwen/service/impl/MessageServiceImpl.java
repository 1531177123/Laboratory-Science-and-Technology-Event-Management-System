package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiwen.common.Constants;
import com.yiwen.domain.*;
import com.yiwen.dao.MessageDao;
import com.yiwen.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.utils.CommonUtils;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-23
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements IMessageService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserLabService userLabService;

    @Autowired
    private IUserTeamService userTeamService;

    @Autowired
    private ITeamService teamService;

    @Override
    public boolean saveMessage(Message msg)
    {
        msg.setCreateTime(DateTimeUtil.getSysTime());
        msg.setStatus("0");
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        msg.setCreatorId(currentUserLogin.getId());
        msg.setCreator(userService.getCurrentUserDetails(currentUserLogin).getName());
        if (StrUtil.isBlank(msg.getToUser()))
        {
            Set<String> userIds = userService.getAllUserLogin().stream().map(UserLogin::getId).collect(Collectors.toSet());
            msg.setToUser(userIds.toString());
        }
        return save(msg);
    }

    @Override
    public boolean updateMessageById(Message msg)
    {
        boolean updateMsgFlag = updateById(msg);
        String type = msg.getType();
        String relationId = msg.getRelationId();
        String status = msg.getStatus();
        boolean updateRelationFlag = updateRelationByType(type, relationId, status, msg.getContent());
        return updateMsgFlag && updateRelationFlag;
    }

    /**
     * 根据类型和关联id处理更新结果
     * @param type 类型
     * @param relationId 关联id
     * @param status 状态
     * @return 结果
     */
    private boolean updateRelationByType(String type, String relationId, String status, String content)
    {
        if (Constants.MEG_TYPE_LAB_APPLY.equals(type))
        {
            UserLab userLab = userLabService.getById(relationId);
            userLab.setStatus(status);
           return userLabService.updateStatus(userLab);
        }
        else if (Constants.MEG_TYPE_TEAM_APPLY.equals(type))
        {
            UserTeam userTeam = userTeamService.getById(relationId);
            userTeam.setStatus(status);
            return userTeamService.updateStatus(userTeam);
        }
        else if (Constants.MEG_TYPE_TEAM_CERTIFICATION_SCORE.equals(type))
        {
            Team team = teamService.getById(relationId);
            String scoreStatus;
            String msgContent;
            String msgType;
            if (Constants.CHECK_PASS.equals(status))
            {
                scoreStatus = Constants.STATUS_TEAM_FINISH;
                team.setScore(content.substring(content.lastIndexOf(":") + 1));
                msgContent = "成绩认证成功";
                msgType = Constants.MESSAGE_TYPE_SUCCESS;
            }
            else
            {
                scoreStatus = Constants.STATUS_TEAM_SCORE_AUDIT_FAIL;
                msgContent = "成绩认证失败";
                msgType = Constants.MESSAGE_TYPE_ERROR;
            }
            team.setStatus(scoreStatus);
            boolean updateTeamFlag = teamService.updateById(team);
            MessageUtils.sendMessageToUser(msgType, "组队成绩认证", msgContent, null, team.getCreatorId());
            return updateTeamFlag;
        }
        return false;
    }

    @Override
    public IPage<Message> selectPage(long currentPage, long pageSize, String type, String creator, String status)
    {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(type), Message::getType, type);
        queryWrapper.eq(StrUtil.isNotBlank(status), Message::getStatus, status);
        queryWrapper.like(StrUtil.isNotBlank(creator), Message::getCreator, creator);
        queryWrapper.orderByDesc(Message::getCreateTime);
        List<Message> messageList = list(queryWrapper);
        Page<Message> messagePage = new Page<>(currentPage, pageSize);
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        if (!Constants.ROLE_ADMIN.equals(currentUserLogin.getRoleType()))
        {

            List<Message> newRecords = new ArrayList<>();
            int addSize = 0;
            for (Message record : messageList)
            {
                String[] splitUserIds = record.getToUser().substring(1, record.getToUser().length() - 1).split(", ");
                record.setToUserIds(Arrays.asList(splitUserIds));
                if (record.getCreatorId().equals(currentUserLogin.getId()) || record.getToUserIds().contains(currentUserLogin.getId()))
                {
                    newRecords.add(record);
                    addSize++;
                }
                if (addSize == pageSize)
                {
                    messagePage.setRecords(new ArrayList<>(newRecords));
                }
            }
            if (messagePage.getRecords().isEmpty())
            {
                messagePage.setRecords(newRecords);
            }
            messagePage.setTotal(addSize);
            return messagePage;
        }

        return messageDao.selectPage(messagePage,queryWrapper);
    }

    @Override
    public boolean pressTeams(String playId)
    {
        boolean saveFlag = false;
        try
        {
            List<Team> teams = teamService.getByPlayId(playId).stream().filter(team -> !Constants.STATUS_TEAM_FINISH.equals(team.getStatus())).collect(Collectors.toList());
            UserLogin currentUserLogin = userService.getCurrentUserLogin();
            String userName = userService.getCurrentUserDetails(currentUserLogin).getName();
            for (Team team : teams)
            {
                Set<String> userIds =  userTeamService.getByTeamId(team.getId()).stream().filter(userTeam -> Constants.CHECK_PASS.equals(userTeam.getStatus()))
                        .map(UserTeam::getUserId).collect(Collectors.toSet());
                String messageContent = userName + "对你参加的[" + team.getName() + "]队伍进行了赛事催促提醒，请注意赛事进度";
                MessageUtils.sendMessageToUsers(Constants.MESSAGE_TYPE_WARNING, "赛事提醒", messageContent
                        , null, new ArrayList<>(userIds));
                Message message = CommonUtils.assembleMessage(Constants.MEG_TYPE_TEAM_PRESS, Constants.CHECK_APPLYING, null
                                                            , currentUserLogin.getId(), userName, messageContent, null, userIds.toString());
                saveFlag  = save(message);
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return saveFlag;
    }

    @Override
    public boolean saveFeedBack(Message msg)
    {
        msg.setType(Constants.MEG_TYPE_FEEDBACK);
        msg.setCreateTime(DateTimeUtil.getSysTime());
        msg.setStatus("0");
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        msg.setCreatorId(currentUserLogin.getId());
        msg.setCreator(userService.getCurrentUserDetails(currentUserLogin).getName());
        Set<String> userIds = new HashSet<>();
        if (StrUtil.isBlank(msg.getToUser()))
        {
            userIds = userService.getAllUserLogin().stream().filter(user -> Constants.ROLE_ADMIN.equals(user.getRoleType())).map(UserLogin::getId).collect(Collectors.toSet());
            msg.setToUser(userIds.toString());
        }
        MessageUtils.sendMessageToUsers(Constants.MESSAGE_TYPE_INFO, "系统反馈", msg.getContent(), Constants.FRONT__PATH_MESSAGE, new ArrayList<>(userIds));
        return save(msg);
    }
}
