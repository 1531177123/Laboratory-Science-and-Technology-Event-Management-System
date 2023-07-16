package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.common.Code;
import com.yiwen.common.Constants;
import com.yiwen.common.Result;
import com.yiwen.dao.LabDao;
import com.yiwen.domain.*;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.ILabService;
import com.yiwen.service.IPlayService;
import com.yiwen.service.IUserLabService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 实验室详细信息表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-21
 */
@Service
public class LabServiceImpl extends ServiceImpl<LabDao, Lab> implements ILabService {

    @Autowired
    private LabDao labDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserLabService userLabService;

    @Autowired
    private IPlayService playService;

    @Override
    public IPage<Lab> selectPage(long currentPage, long pageSize, String name, String type, String code, String school, String college)
    {
        LambdaQueryWrapper<Lab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Lab::getName, name);
        queryWrapper.like(StrUtil.isNotBlank(school), Lab::getSchool, school);
        queryWrapper.like(StrUtil.isNotBlank(college), Lab::getCollege, college);
        queryWrapper.eq(StrUtil.isNotBlank(type), Lab::getType, type);
        queryWrapper.eq(StrUtil.isNotBlank(code), Lab::getCode, code);
        return getLabIPage(currentPage, pageSize, queryWrapper);
    }


    @Override
    public boolean saveLab(Lab lab)
    {
       return handleSave(lab, null);
    }

    private boolean handleSave(Lab lab, String oldUsername)
    {
        String code = lab.getCode();
        String name = lab.getName();
        if (StrUtil.isBlank(code) || StrUtil.isBlank(name))
        {
            throw new BusinessException(Code.SAVE_ERR, "名称或者实验室代码不能为空！");
        }
        LambdaQueryWrapper<Lab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Lab::getCode, code).or().eq(Lab::getName, name);
        Lab sLab = getOne(queryWrapper);
        if (sLab != null)
        {
            throw new BusinessException(Code.SAVE_ERR, "名称或实验室编码重复！");
        }
        lab.setCreateTime(DateTimeUtil.getSysTime());
        UserLogin creatorUserLogin = StrUtil.isBlank(oldUsername) ? userService.getCurrentUserLogin() : userService.getLoginByUserName(oldUsername);
        lab.setCreatorId(creatorUserLogin.getId());
        lab.setCreator(userService.getCurrentUserDetails(creatorUserLogin).getName());
        boolean saveFlag = save(lab);
        //TODO 将创建人和实验室的关系创建记录在实验室和人员关系表中
        UserLab userLab = new UserLab();
        userLab.setStatus("2");
        userLab.setLabRole("2");
        userLab.setUserId(creatorUserLogin.getId());
        userLab.setLabId(lab.getId());
        userLab.setCreateTime(DateTimeUtil.getSysTime());
        boolean saveRelationFlag = userLabService.save(userLab);

        return saveFlag && saveRelationFlag;
    }

    @Override
    public boolean updateLabById(Lab lab)
    {
        LambdaQueryWrapper<Lab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(lab.getCode()), Lab::getCode, lab.getCode()).
                or().eq(StrUtil.isNotBlank(lab.getName()), Lab::getName, lab.getName());
        Lab oneLab = getOne(queryWrapper);
        if (!oneLab.getId().equals(lab.getId()))
        {
            throw new BusinessException(Code.SAVE_ERR, "名称或实验室编码重复！");
        }
        lab.setEditTime(DateTimeUtil.getSysTime());
        return updateById(lab);
    }

    @Override
    public IPage<Lab> selectPageByUserId(long currentPage, long pageSize, String userId)
    {
        return getLabIPage(currentPage, pageSize, getLabsQueryByUserId(userId));
    }

    @Override
    public List<Lab> getAll(String userId, String name, String priority)
    {

        List<Lab> labs = list(getLabsQueryByUserId(userId));
        for (Lab lab : labs)
        {
            List<Play> plays = playService.getByLabIdAndMix(lab.getId(), name, priority);
            lab.setPlays(plays);
        }
        return setPersonNumber(labs);
    }

    @Override
    public Lab getLabById(String id)
    {
        List<Lab> labs = new ArrayList<>();
        labs.add(getById(id));
        setPersonNumber(labs);
        return labs.get(0);
    }

    @Override
    public Result importLabInfo(List<Lab> labs)
    {
        try
        {
            for (Lab lab : labs)
            {
                String[] split = lab.getMemberNamesStr().trim().split(",");
                String creatorUserName = lab.getCreator();
                if (!handleSave(lab, creatorUserName))
                {
                    throw new BusinessException(Code.BUSINESS_ERR, "批量导入实验室信息失败,请检查数据重试");
                }
                for (String username : split)
                {
                    String tmpUsername = username.trim();
                    if (Objects.equals(tmpUsername, creatorUserName))
                    {
                        continue;
                    }
                    UserLab userLab = new UserLab();
                    userLab.setStatus(Constants.CHECK_PASS);
                    userLab.setCreateTime(DateTimeUtil.getSysTime());
                    userLab.setLabId(lab.getId());
                    userLab.setUserId( userService.getLoginByUserName(tmpUsername).getId());
                    userLab.setLabRole(Constants.TEAM_ROLE_COMMON);
                    if (!userLabService.save(userLab))
                    {
                        throw new BusinessException(Code.BUSINESS_ERR, "批量导入实验室信息失败,请检查数据重试");
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("批量导入用户账号信息失败：" + e);
            return new Result(Code.UPDATE_ERR, null, "批量导入实验室信息失败,请检查数据重试");
        }
        return new Result(Code.COMMON_OK, null, "导入实验室信息成功");
    }

    @Override
    public List<Lab> getExportAll()
    {
        List<Lab> labs = list();

        for (Lab lab : labs)
        {
            List<String> members = new ArrayList<>();
            Set<String> userIds = userLabService.getByLabId(lab.getId()).stream().filter(userLab -> Constants.CHECK_PASS.equals(userLab.getStatus()))
                    .map(UserLab::getUserId).collect(Collectors.toSet());
          if (userIds.size() > 0)
          {
              for (String userId : userIds)
              {
                  UserLogin loginById = userService.getLoginById(userId);
                  UserDetail currentUserDetails = userService.getCurrentUserDetails(loginById);
                  members.add(currentUserDetails.getName());
              }
          }
          lab.setMemberNames(members);
        }
        return labs;
    }

    private LambdaQueryWrapper<Lab> getLabsQueryByUserId(String userId) {
        List<String> labIds = userLabService.getByUserId(userId).stream().filter(v -> Constants.CHECK_PASS.equals(v.getStatus()))
                .map(UserLab::getLabId).collect(Collectors.toList());
        LambdaQueryWrapper<Lab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Lab::getId, labIds);
        queryWrapper.orderByDesc(Lab::getCreateTime);
        return queryWrapper;
    }

    /**
     * 设置实验室人数
     * @param labs 实验室
     * @return 结果
     */
    private List<Lab> setPersonNumber(List<Lab> labs)
    {
        List<UserLab> userLabs = userLabService.list().stream().filter(v -> Constants.CHECK_PASS.equals(v.getStatus())).collect(Collectors.toList());

        for (Lab lab : labs) {
            int personNumber = 0;
            Set<String> userIds = new HashSet<>();
            for (UserLab userLab : userLabs) {
                if (userLab.getLabId().equals(lab.getId()) && !userIds.contains(userLab.getUserId()))
                {
                    personNumber++;
                    userIds.add(userLab.getUserId());
                }
            }
            lab.setPersonNumber(String.valueOf(personNumber));
        }
        return labs;
    }

    /**
     * 处理分页条件查询
     * @param currentPage 当前页
     * @param pageSize 每页记录数
     * @param queryWrapper 查询条件
     * @return 结果
     */
    private IPage<Lab> getLabIPage(long currentPage, long pageSize, LambdaQueryWrapper<Lab> queryWrapper)
    {
        queryWrapper.orderByDesc(Lab::getCreateTime);
        Page<Lab> labPage = labDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        List<Lab> records = setPersonNumber(labPage.getRecords());
        labPage.setRecords(records);
        return labPage;
    }

}
