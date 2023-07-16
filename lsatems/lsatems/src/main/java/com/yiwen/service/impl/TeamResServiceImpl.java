package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.dao.TeamResDao;
import com.yiwen.domain.Files;
import com.yiwen.domain.PlayRes;
import com.yiwen.domain.TeamRes;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.IFileService;
import com.yiwen.service.ITeamResService;
import com.yiwen.service.ITeamService;
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
 * @since 2023-03-26
 */
@Service
public class TeamResServiceImpl extends ServiceImpl<TeamResDao, TeamRes> implements ITeamResService
{
    @Autowired
    private IFileService fileService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITeamService teamService;

    @Autowired
    private TeamResDao teamResDao;

    @Override
    public boolean savePlayRes(MultipartFile file, String teamId) throws Exception
    {
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        Files saveFiles = fileService.uploadRes(file, currentUserLogin.getId());
        TeamRes teamRes = new TeamRes();
        teamRes.setFileId(saveFiles.getId());
        teamRes.setCreateTime(DateTimeUtil.getSysTime());
        teamRes.setTeamId(teamId);
        return save(teamRes);
    }

    @Override
    public IPage<TeamRes> selectPage(long currentPage, long pageSize, String teamId)
    {
        LambdaQueryWrapper<TeamRes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(teamId), TeamRes::getTeamId, teamId);
        IPage<TeamRes> iPage = teamResDao.selectPage(new Page<TeamRes>(currentPage, pageSize), queryWrapper);
        List<TeamRes> records = iPage.getRecords();
        for (TeamRes record : records)
        {
            record.setTeamName(teamService.getById(record.getTeamId()).getName());
            Files files = fileService.getById(record.getFileId());
            UserLogin loginById = userService.getLoginById(files.getCreatorId());
            files.setCreator(userService.getCurrentUserDetails(loginById).getName());
            record.setResource(files);
        }
        iPage.setRecords(records);
        return iPage;
    }
}
