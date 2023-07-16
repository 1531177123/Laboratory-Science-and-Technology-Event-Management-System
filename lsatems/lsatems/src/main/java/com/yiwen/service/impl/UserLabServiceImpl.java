package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.common.Code;
import com.yiwen.common.Constants;
import com.yiwen.controller.dto.UserShowDTO;
import com.yiwen.dao.UserLabDao;
import com.yiwen.domain.*;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.*;
import com.yiwen.utils.CommonUtils;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.MessageUtils;
import org.apache.commons.collections4.BagUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户与实验室关系表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-22
 */
@Service
public class UserLabServiceImpl extends ServiceImpl<UserLabDao, UserLab> implements IUserLabService {

    @Autowired
    private UserLabDao userLabDao;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ILabService labService;

    @Override
    public boolean saveById(String userId, String labId, String content)
    {
        UserLab userLab = new UserLab();
        userLab.setCreateTime(DateTimeUtil.getSysTime());
        userLab.setLabId(labId);
        userLab.setUserId(userId);
        userLab.setLabRole("0");
        userLab.setStatus("0");
        boolean saveRelationFlag = save(userLab);
        String creator = userService.getCurrentUserDetails(userService.getCurrentUserLogin()).getName();
        Lab lab = labService.getById(labId);
        String messageContent = CommonUtils.assembleMessageContent(creator, lab.getName(), content);
        List<String> adminIds = getLabAdminIds(labId);
        Message message = CommonUtils.assembleMessage(Constants.MEG_TYPE_LAB_APPLY, Constants.CHECK_APPLYING, userLab.getId()
                                                    , userId, creator, messageContent, null, adminIds.toString());
        boolean saveMsgFlag = messageService.saveMessage(message);
        if (saveRelationFlag && saveMsgFlag)
        {

            MessageUtils.sendMessageToUsers(Constants.MESSAGE_TYPE_WARNING, "实验室申请", messageContent, Constants.FRONT__PATH_MESSAGE, adminIds);
        }
        return saveRelationFlag && saveMsgFlag;
    }
    @Override
    public List<String> getLabAdminIds(String labId)
    {
        LambdaQueryWrapper<UserLab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(labId), UserLab::getLabId, labId);
        queryWrapper.ne(UserLab::getLabRole, Constants.TEAM_ROLE_COMMON);
        return list(queryWrapper).stream().map(UserLab::getUserId).collect(Collectors.toList());
    }

    @Override
    public boolean updateStatus(UserLab userLab)
    {
        boolean updateRelationFlag = updateById(userLab);
        String labId = userLab.getLabId();
        Lab lab = labService.getById(labId);
        lab.setEditTime(DateTimeUtil.getSysTime());
        boolean updateLabFlag = labService.updateById(lab);
        String type = Constants.CHECK_PASS.equals(userLab.getStatus()) ? Constants.MESSAGE_TYPE_SUCCESS : Constants.MESSAGE_TYPE_ERROR;
        String message = Constants.CHECK_PASS.equals(userLab.getStatus()) ? "申请通过" : "申请失败";
        MessageUtils.sendMessageToUser(type, "实验室申请", lab.getName() + message, null, userLab.getUserId());
        return updateRelationFlag && updateLabFlag;
    }

    @Override
    public List<UserLab> getByUserId(String userId)
    {
        LambdaQueryWrapper<UserLab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(userId), UserLab::getUserId, userId);
        return list(queryWrapper);
    }

    @Override
    public boolean quitLab(String userId, String labId)
    {
        LambdaQueryWrapper<UserLab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLab::getUserId, userId);
        queryWrapper.eq(UserLab::getLabId, labId);
        return remove(queryWrapper);
    }

    @Override
    public List<UserLab> getByLabId(String labId)
    {
        LambdaQueryWrapper<UserLab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(labId), UserLab::getLabId, labId);
        queryWrapper.orderByDesc(UserLab::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<UserShowDTO> getMembers(String labId)
    {
        List<UserShowDTO> labMembers = new ArrayList<>();
        LambdaQueryWrapper<UserLab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLab::getLabId, labId);
        queryWrapper.eq(UserLab::getStatus, Constants.CHECK_PASS);
        queryWrapper.orderByDesc(UserLab::getCreateTime);
        List<UserLab> userLabList = list(queryWrapper);
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        if (!Constants.ROLE_ADMIN.equals(currentUserLogin.getRoleType()))
        {
            boolean adminFlag = userLabList.stream()
                    .anyMatch(userLab -> userLab.getUserId().equals(currentUserLogin.getId()) && !Constants.TEAM_ROLE_COMMON.equals(userLab.getLabRole()));
            if (!adminFlag)
            {
                return null;
            }
        }
        for (UserLab userLab : userLabList)
        {
            UserShowDTO userShowDTO = new UserShowDTO();
            UserLogin loginById = userService.getLoginById(userLab.getUserId());
            userShowDTO.setId(loginById.getId());
            UserDetail currentUserDetails = userService.getCurrentUserDetails(loginById);
            userShowDTO.setAvatarPath(currentUserDetails.getAvatarPath());
            userShowDTO.setName(currentUserDetails.getName());
            userShowDTO.setRoleType(userLab.getLabRole());
            labMembers.add(userShowDTO);
        }
        return labMembers;
    }

    @Override
    public List<UserShowDTO> commissionAdmin(String labId, String userId)
    {
        if (StrUtil.isBlank(labId) || StrUtil.isBlank(userId))
        {
            throw new BusinessException(Code.BUSINESS_ERR, "获取操作对象失败，请检查网络后重试");
        }
        LambdaUpdateWrapper<UserLab> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserLab::getLabId, labId);
        updateWrapper.eq(UserLab::getUserId, userId);
        updateWrapper.set(UserLab::getLabRole, Constants.TEAM_ROLE_ADMIN);
        boolean updateFlag = update(null, updateWrapper);
        List<UserShowDTO> resMembers = new ArrayList<>();
        if (updateFlag)
        {
            resMembers= getMembers(labId);
        }
        return resMembers;
    }

    @Override
    public List<UserShowDTO> cancelCommissionAdmin(String labId, String userId)
    {
        if (StrUtil.isBlank(labId) || StrUtil.isBlank(userId))
        {
            throw new BusinessException(Code.BUSINESS_ERR, "获取操作对象失败，请检查网络后重试");
        }
        LambdaUpdateWrapper<UserLab> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserLab::getLabId, labId);
        updateWrapper.eq(UserLab::getUserId, userId);
        updateWrapper.set(UserLab::getLabRole, Constants.TEAM_ROLE_COMMON);
        boolean updateFlag = update(null, updateWrapper);
        List<UserShowDTO> resMembers = new ArrayList<>();
        if (updateFlag)
        {
            resMembers= getMembers(labId);
        }
        return resMembers;
    }

    @Override
    public List<UserShowDTO> forcePropose(String labId, String userId)
    {
        if (StrUtil.isBlank(labId) || StrUtil.isBlank(userId))
        {
            throw new BusinessException(Code.BUSINESS_ERR, "获取操作对象失败，请检查网络后重试");
        }
        LambdaQueryWrapper<UserLab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLab::getUserId, userId);
        queryWrapper.eq(UserLab::getLabId, labId);
        boolean removeFlag = remove(queryWrapper);
        List<UserShowDTO> resMembers = new ArrayList<>();
        if (removeFlag)
        {
            resMembers= getMembers(labId);
        }
        return resMembers;
    }
}
