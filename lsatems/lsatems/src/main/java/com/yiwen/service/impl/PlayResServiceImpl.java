package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.dao.PlayResDao;
import com.yiwen.domain.Files;
import com.yiwen.domain.PlayRes;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.IFileService;
import com.yiwen.service.IPlayResService;
import com.yiwen.service.IPlayService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
@Service
public class PlayResServiceImpl extends ServiceImpl<PlayResDao, PlayRes> implements IPlayResService
{

    @Autowired
    private PlayResDao playResDao;

    @Autowired
    private IFileService fileService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IPlayService playService;

    @Override
    public boolean savePlayRes(MultipartFile file, String playId) throws Exception
    {
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        Files saveFiles = fileService.uploadRes(file, currentUserLogin.getId());
        PlayRes playRes = new PlayRes();
        playRes.setFileId(saveFiles.getId());
        playRes.setCreateTime(DateTimeUtil.getSysTime());
        playRes.setPlayId(playId);
        return save(playRes);
    }

    @Override
    public IPage<PlayRes> selectPage(long currentPage, long pageSize, String playId)
    {
        LambdaQueryWrapper<PlayRes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(playId), PlayRes::getPlayId, playId);
        IPage<PlayRes> iPage = playResDao.selectPage(new Page<PlayRes>(currentPage, pageSize), queryWrapper);
        List<PlayRes> records = iPage.getRecords();
        for (PlayRes record : records)
        {
            record.setPlayName(playService.getById(record.getPlayId()).getName());
            Files files = fileService.getById(record.getFileId());
            UserLogin loginById = userService.getLoginById(files.getCreatorId());
            files.setCreator(userService.getCurrentUserDetails(loginById).getName());
            record.setResource(files);
        }
        iPage.setRecords(records);
        return iPage;
    }
}
