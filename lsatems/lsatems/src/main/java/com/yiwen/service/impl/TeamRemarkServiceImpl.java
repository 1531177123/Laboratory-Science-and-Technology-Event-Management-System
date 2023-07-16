package com.yiwen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.dao.TeamRemarkDao;
import com.yiwen.domain.TeamRemark;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.ITeamRemarkService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 科技赛事评论表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
@Service
public class TeamRemarkServiceImpl extends ServiceImpl<TeamRemarkDao, TeamRemark> implements ITeamRemarkService
{

    @Autowired
    private TeamRemarkDao teamRemarkDao;

    @Autowired
    private IUserService userService;

    @Override
    public boolean saveTeamRemark(TeamRemark playRemark)
    {
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        playRemark.setUserId(currentUserLogin.getId());
        playRemark.setAvatarPath(userService.getCurrentUserDetails(currentUserLogin).getAvatarPath());
        playRemark.setCreateTime(DateTimeUtil.getSysTime());
        if (playRemark.getPid() != null)
        {
            String pid = playRemark.getPid();
            TeamRemark pTeamRemark = getById(pid);
            if (pTeamRemark.getOriginalId() != null)
            {
                playRemark.setOriginalId(pTeamRemark.getOriginalId());
            }
            else{
                playRemark.setOriginalId(playRemark.getPid());
            }
        }
        return save(playRemark);
    }

    @Override
    public boolean updateTeamRemarkById(TeamRemark playRemark)
    {
        playRemark.setEditTime(DateTimeUtil.getSysTime());
        return updateById(playRemark);
    }

    @Override
    public List<TeamRemark> getTreeByTeamId(String teamId)
    {
        List<TeamRemark> playRemarks = teamRemarkDao.findRemarkDetails(teamId);
        List<TeamRemark> originalList = playRemarks.stream().filter(a -> a.getOriginalId() == null).collect(Collectors.toList());

        for (TeamRemark original : originalList) {
            List<TeamRemark> remarks = playRemarks.stream().filter(remark -> original.getId().equals(remark.getOriginalId())).collect(Collectors.toList());
            remarks.forEach(playRemark -> {
                playRemarks.stream().filter(c1 -> c1.getId().equals(playRemark.getPid())).findFirst().ifPresent(v -> {
                    playRemark.setPUserId(v.getUserId());
                    playRemark.setPName(v.getName());
                });
            });
            original.setChildren(remarks);
        }

        return originalList;
    }
}
