package com.yiwen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.dao.PlayRemarkDao;
import com.yiwen.domain.ArticleRemark;
import com.yiwen.domain.PlayRemark;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.IPlayRemarkService;
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
public class PlayRemarkServiceImpl extends ServiceImpl<PlayRemarkDao, PlayRemark> implements IPlayRemarkService
{

    @Autowired
    private PlayRemarkDao playRemarkDao;

    @Autowired
    private IUserService userService;

    @Override
    public boolean savePlayRemark(PlayRemark playRemark)
    {
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        playRemark.setUserId(currentUserLogin.getId());
        playRemark.setAvatarPath(userService.getCurrentUserDetails(currentUserLogin).getAvatarPath());
        playRemark.setCreateTime(DateTimeUtil.getSysTime());
        if (playRemark.getPid() != null)
        {
            String pid = playRemark.getPid();
            PlayRemark pPlayRemark = getById(pid);
            if (pPlayRemark.getOriginalId() != null)
            {
                playRemark.setOriginalId(pPlayRemark.getOriginalId());
            }
            else{
                playRemark.setOriginalId(playRemark.getPid());
            }
        }
        return save(playRemark);
    }

    @Override
    public boolean updatePlayRemarkById(PlayRemark playRemark)
    {
        playRemark.setEditTime(DateTimeUtil.getSysTime());
        return updateById(playRemark);
    }

    @Override
    public List<PlayRemark> getTreeByPlayId(String playId)
    {
        List<PlayRemark> playRemarks = playRemarkDao.findRemarkDetails(playId);
        List<PlayRemark> originalList = playRemarks.stream().filter(a -> a.getOriginalId() == null).collect(Collectors.toList());

        for (PlayRemark original : originalList) {
            List<PlayRemark> remarks = playRemarks.stream().filter(remark -> original.getId().equals(remark.getOriginalId())).collect(Collectors.toList());
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
