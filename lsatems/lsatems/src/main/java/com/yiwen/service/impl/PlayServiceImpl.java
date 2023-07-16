package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiwen.common.Constants;
import com.yiwen.domain.*;
import com.yiwen.dao.PlayDao;
import com.yiwen.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.utils.CommonUtils;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 科技赛事详细信息表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-24
 */
@Service
public class PlayServiceImpl extends ServiceImpl<PlayDao, Play> implements IPlayService
{

    @Autowired
    private PlayDao playDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private ILabService labService;

    @Autowired
    private IUserLabService userLabService;

    @Autowired
    private IMessageService messageService;

    @Override
    public IPage<Play> selectPage(long currentPage, long pageSize, String name, String labId, String priority)
    {
        LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Play::getName, name);
        queryWrapper.eq(StrUtil.isNotBlank(labId), Play::getLabId, labId);
        queryWrapper.eq(StrUtil.isNotBlank(priority), Play::getPriority, priority);
        queryWrapper.orderByDesc(Play::getCreateTime);
        Page<Play> playPage = playDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        List<Play> records = playPage.getRecords();
        playPage.setRecords(setPlayLabName(records));
        return playPage;
    }

    /**
     * 设置结果的实验室名称
     * @param playPages 查询结果
     * @return 结果
     */
    private List<Play> setPlayLabName(List<Play> playPages)
    {
        for (Play record : playPages) {
            String labId = record.getLabId();
            record.setLabName(labService.getById(labId).getName());
        }
        return playPages;
    }

    @Override
    public boolean savePlay(Play play)
    {
        play.setCreateTime(DateTimeUtil.getSysTime());
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        String creatorId = currentUserLogin.getId();
        String creator = userService.getCurrentUserDetails(currentUserLogin).getName();
        play.setCreatorId(creatorId);
        play.setCreator(creator);
        boolean saveFlag = save(play);
        String labId = play.getLabId();
        Lab lab = labService.getById(labId);
        Set<String> userIds = userLabService.getByLabId(labId).stream().filter(userLab -> Constants.CHECK_PASS.equals(userLab.getStatus()))
                                .map(UserLab::getUserId).collect(Collectors.toSet());
        String messageContent = creator + "发布了新的科技赛事【" + play.getName() +"】,请大家注意关注！";
        MessageUtils.sendMessageToUsers(Constants.MESSAGE_TYPE_WARNING, "赛事发布", messageContent, null, new ArrayList<>(userIds));
        Message message = CommonUtils.assembleMessage(Constants.MEG_TYPE_PLAY_ANNOUNCE, Constants.CHECK_APPLYING, null, creatorId
                                                    , creator, messageContent, null, userIds.toString());
        boolean saveMessage = messageService.saveMessage(message);
        return saveFlag && saveMessage;
    }

    @Override
    public boolean updatePlayById(Play play)
    {
        play.setEditTime(DateTimeUtil.getSysTime());
        return updateById(play);
    }

    @Override
    public List<Play> getByLabId(String labId)
    {
        LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StrUtil.isNotBlank(labId), Play::getLabId, labId);
        queryWrapper.orderByDesc(Play::getCreateTime);
        return setPlayLabName(list(queryWrapper));
    }

    @Override
    public List<Play> getByLabIdAndMix(String labId, String name, String priority)
    {
        LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StrUtil.isNotBlank(labId), Play::getLabId, labId);
        queryWrapper.like(StrUtil.isNotBlank(name), Play::getName, name);
        queryWrapper.eq(StrUtil.isNotBlank(priority), Play::getPriority, priority);
        queryWrapper.orderByDesc(Play::getCreateTime);
        return setPlayLabName(list(queryWrapper));
    }

}
