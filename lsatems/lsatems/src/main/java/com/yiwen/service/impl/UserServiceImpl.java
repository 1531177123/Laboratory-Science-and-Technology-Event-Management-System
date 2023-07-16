package com.yiwen.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiwen.common.Code;
import com.yiwen.common.Constants;
import com.yiwen.common.Result;
import com.yiwen.controller.dto.UserLoginDTO;
import com.yiwen.controller.dto.UserPasswordDTO;
import com.yiwen.controller.dto.UserShowDTO;
import com.yiwen.dao.RoleMenuDao;
import com.yiwen.dao.UserDetailDao;
import com.yiwen.dao.UserLoginDao;
import com.yiwen.domain.Menu;
import com.yiwen.domain.UserDetail;
import com.yiwen.domain.UserLogin;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.IMenuService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.MD5Util;
import com.yiwen.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 论坛文章信息表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@Service
public class UserServiceImpl  implements IUserService
{

    @Autowired
    private UserLoginDao userLoginDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private RoleMenuDao roleMenuDao;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserLoginDTO login(UserLoginDTO userLoginDTO)
    {
        LambdaQueryWrapper<UserLogin> q = new LambdaQueryWrapper<>();
        q.eq(UserLogin::getUsername, userLoginDTO.getUsername()).eq(UserLogin::getPassword, MD5Util.getMD5(userLoginDTO.getPassword()));
        UserLogin userLogin ;
        try
        {
            userLogin = userLoginDao.selectOne(q);
        }
        catch (Exception e)
        {
            System.err.println("登录查询单条结果出错!");
            throw new BusinessException(Code.BUSINESS_ERR, "登录查询单条结果出错");
        }
        if (userLogin != null)
        {
            String roleType = userLogin.getRoleType();
            userLogin.setLastLoginTime(DateTimeUtil.getSysTime());
            userLoginDao.updateById(userLogin);
            userLoginDTO.setToken(TokenUtils.getToken(userLogin.getId(), userLogin.getPassword()));
            userLoginDTO.setRoleType(roleType);
            userLoginDTO.setDetailFlag(userLogin.getDetailFlag());
            userLoginDTO.setMenus(getRoleMenus(roleType));
            userLoginDTO.setId(userLogin.getId());
            return userLoginDTO;
        }
        else
        {
            throw new BusinessException(Code.BUSINESS_ERR, "用户名或者密码错误");
        }
    }

    @Override
    public Boolean register(Map<String, String> userInfo)
    {
        UserLogin  userLogin = new UserLogin();
        String sysTime = DateTimeUtil.getSysTime();
        userLogin.setCreateTime(sysTime);
        userLogin.setDeleted("0");
        userLogin.setLastLoginTime(sysTime);
        userLogin.setRoleType(userInfo.getOrDefault("role", "0"));
        userLogin.setUsername(userInfo.get("username"));
        userLogin.setPassword(MD5Util.getMD5(userInfo.get("password")));
        userLogin.setDetailFlag("0");
        LambdaQueryWrapper<UserLogin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLogin::getUsername, userLogin.getUsername());
        List<UserLogin> userLogins = userLoginDao.selectList(queryWrapper);
        if (!userLogins.isEmpty())
        {
            throw new BusinessException(Code.BUSINESS_ERR, "账号已存在！！");
        }
        int count = userLoginDao.insert(userLogin);
        return count == 1;
    }

    @Override
    public Result save(UserLogin userInfo)
    {
        String sysTime = DateTimeUtil.getSysTime();
        userInfo.setCreateTime(sysTime);
        userInfo.setDeleted("0");
        userInfo.setLastLoginTime(sysTime);
        userInfo.setDetailFlag("0");
        if (StringUtils.isBlank(userInfo.getUsername()))
        {
            return new Result(Code.SAVE_ERR, null, "用户名不能为空！");
        }
        LambdaQueryWrapper<UserLogin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLogin::getUsername, userInfo.getUsername());
        if (userLoginDao.selectOne(queryWrapper) != null)
        {
            return new Result(Code.SAVE_ERR, null, "用户名已存在");
        }
        String password = userInfo.getPassword();
        if (StringUtils.isNotBlank(password))
        {
            userInfo.setPassword(MD5Util.getMD5(password));
        }
        if (userLoginDao.insert(userInfo) == 1)
        {
            return new Result(Code.SAVE_OK, null, "新增用户成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增用户失败");
    }

    @Override
    public Result update(UserLogin userInfo)
    {
        userInfo.setEditTime(DateTimeUtil.getSysTime());
        if (StringUtils.isNotBlank(userInfo.getUsername()))
        {
            LambdaQueryWrapper<UserLogin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserLogin::getUsername, userInfo.getUsername());
            queryWrapper.ne(UserLogin::getId, userInfo.getId());
            if (userLoginDao.selectOne(queryWrapper) != null)
            {
                return new Result(Code.UPDATE_ERR, null, "用户名已存在");
            }
        }
        String password = userInfo.getPassword();
        if (StringUtils.isNotBlank(password))
        {
            userInfo.setPassword(MD5Util.getMD5(password));
        }
        if (userLoginDao.updateById(userInfo) == 1)
        {
            //更新缓存
            updateRedisCache(Constants.REDIS_KEY_ACCOUNT_ID, userInfo.getId(), null);
            return new Result(Code.UPDATE_OK, null, "更新用户信息成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新用户信息失败");
    }

    @Override
    public Boolean delete(String id)
    {
        if (userLoginDao.deleteById(id) == 1)
        {
            //更新缓存
            updateRedisCache(Constants.REDIS_KEY_ACCOUNT_ID, id, null);
            return true;
        }
        return false;
    }

    @Override
    public UserLogin selectById(String id)
    {
        UserLogin userLogin = null;
        //查询redis缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(Constants.REDIS_KEY_ACCOUNT_ID + id);

        if (StrUtil.isBlank(jsonStr))
        {
            userLogin = userLoginDao.selectById(id);
            stringRedisTemplate.opsForValue().set(Constants.REDIS_KEY_ACCOUNT_ID + id, JSONUtil.toJsonStr(userLogin));
        }
        else
        {
            userLogin = JSONUtil.toBean(jsonStr, new TypeReference<UserLogin>() {
            }, true);
        }
        return userLogin;
    }

    @Override
    public IPage<UserLogin> selectPage(long currentPage, long pageSize, String roleType, String username, String detailFlag)
    {
        LambdaQueryWrapper<UserLogin> lambdaQueryWrapper = null;
        lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(roleType),UserLogin::getRoleType, roleType);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(username),UserLogin::getUsername, username);
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(detailFlag),UserLogin::getDetailFlag, detailFlag);
        lambdaQueryWrapper.orderByDesc(UserLogin::getLastLoginTime);
        IPage<UserLogin> iPage = userLoginDao.selectPage(new Page<UserLogin>(currentPage, pageSize), lambdaQueryWrapper);
        return iPage;
    }

    @Override
    public Boolean deleteBatchByIds(List<String> ids)
    {
        if (ids == null || ids.isEmpty())
        {
            return false;
        }
        //更新缓存
        updateRedisCache(Constants.REDIS_KEY_ACCOUNT_ID, "*", null);
        return userLoginDao.deleteBatchIds(ids) == ids.size();
    }

    @Override
    public List<UserLogin> selectAll()
    {
        return userLoginDao.selectList(null);
    }

    @Override
    public Result importUserLoginInfo(List<UserLogin> userInfo)
    {
        try
        {
            Set<String> names = new HashSet<>();
            for (UserLogin userLogin : userInfo)
            {
                LambdaQueryWrapper<UserLogin> q = new LambdaQueryWrapper<>();
                String curName = userLogin.getUsername();
                if (StringUtils.isBlank(curName))
                {
                    return new Result(Code.UPDATE_ERR, null, "导入用户账号信息失败,用户名不能为空");
                }

                q.eq(UserLogin::getUsername, userLogin.getUsername());
                if (names.contains(curName) || userLoginDao.selectOne(q) != null)
                {
                    return new Result(Code.UPDATE_ERR, null, "导入用户账号信息失败,存在重复的用户名");
                }
                names.add(curName);
            }

            for (UserLogin userLogin : userInfo)
            {
                if (Code.SAVE_ERR.equals(save(userLogin).getCode()))
                {
                    throw new Exception("批量导入用户账号信息失败");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("批量导入用户账号信息失败：" + e);
            return new Result(Code.UPDATE_ERR, null, "导入用户账号信息失败");
        }
        return new Result(Code.COMMON_OK, null, "导入用户账号信息成功");
    }

    @Override
    public Result saveDetails(UserDetail userInfo)
    {
        try
        {
            userInfo.setCreateTime(DateTimeUtil.getSysTime());
            UserLogin userAccount = getCurrentUserLogin();
            userAccount.setEditTime(DateTimeUtil.getSysTime());
            userAccount.setDetailFlag("1");
            String userId = userAccount.getId();
            userInfo.setUserId(userId);
            Result checkRes = checkUserDetails(userInfo);
            if (Code.COMMON_ERR.equals(checkRes.getCode()))
            {
                return checkRes;
            }
            if ( userLoginDao.updateById(userAccount) == 1 && userDetailDao.insert(userInfo) == 1)
            {
                return checkRes;
            }
        }
        catch (Exception e)
        {
            return new Result(Code.SAVE_ERR, null, "新增用户详细信息失败!");
        }
        return new Result(Code.SAVE_ERR, null, "新增用户详细信息失败!");
    }

    @Override
    public Result findBack(String name, String email, String tel, String newPass)
    {
        LambdaQueryWrapper<UserDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetail::getName, name);
        queryWrapper.eq(UserDetail::getEmail, email);
        queryWrapper.eq(UserDetail::getTel, tel);
        UserDetail account = userDetailDao.selectOne(queryWrapper);
        if ( account != null)
        {
            String accountId = account.getUserId();
            UserLogin userLogin = userLoginDao.selectById(accountId);
            userLogin.setEditTime(DateTimeUtil.getSysTime());
            userLogin.setPassword(MD5Util.getMD5(newPass));
            if (userLoginDao.updateById(userLogin) == 1)
            {
                //更新缓存
                updateRedisCache(Constants.REDIS_KEY_ACCOUNT_ID, userLogin.getId(), null);
                return new Result(Code.COMMON_OK, null , "账号找回成功!");
            }
        }
        return new Result(Code.COMMON_ERR, null , "账号找回失败!");
    }

    @Override
    public Result getShowInfo()
    {
        UserLogin currentUserLogin = getCurrentUserLogin();
        UserDetail userDetail = getCurrentUserDetails(currentUserLogin);
        UserShowDTO userShowDTO = new UserShowDTO();
        userShowDTO.setRoleType(currentUserLogin.getRoleType());
        userShowDTO.setName(userDetail.getName());
        userShowDTO.setAvatarPath(userDetail.getAvatarPath());
        return new Result(Code.COMMON_OK, userShowDTO, "获取用户基本信息成功");
    }

    @Override
    public Result getDetails()
    {
        UserLogin currentUserLogin = getCurrentUserLogin();
        UserDetail userDetail = getCurrentUserDetails(currentUserLogin);
        return new Result(Code.COMMON_OK, userDetail, "获取当前用户个人信息成功");
    }

    @Override
    public Result updateDetails(UserDetail userDetail)
    {
        userDetail.setEditTime(DateTimeUtil.getSysTime());
        if (userDetailDao.updateById(userDetail) == 1)
        {
            //更新缓存
            updateRedisCache(Constants.REDIS_KEY_USER_DETAIL_ID, userDetail.getUserId(), null);
            return new Result(Code.UPDATE_OK, null, "更新用户个人信息成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新用户个人信息失败");
    }

    private Result checkUserDetails(UserDetail userDetail) throws Exception
    {
        LambdaQueryWrapper<UserDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetail::getUserId, userDetail.getUserId());
        UserDetail userDetails = userDetailDao.selectOne(queryWrapper);
        if (userDetailDao.selectOne(queryWrapper) != null)
        {
            return new Result(Code.COMMON_ERR, null, "用户已完善信息，请勿重复操作！");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetail::getName, userDetail.getName());
        queryWrapper.eq(UserDetail::getEmail, userDetail.getEmail());
        queryWrapper.eq(UserDetail::getTel, userDetail.getTel());
        UserDetail userDetail1 = userDetailDao.selectOne(queryWrapper);
        if (userDetail1 != null)
        {
            return new Result(Code.COMMON_ERR, null, "用户已注册,请勿重复操作！");
        }
        return new Result(Code.SAVE_OK, null, "新增用户详细信息成功!");
    }

    @Override
    public UserLogin getCurrentUserLogin()
    {
        UserLogin currentUserLoginInfo = TokenUtils.getCurrentUserLoginInfo();
        if (currentUserLoginInfo == null)
        {
            throw new BusinessException(Code.COMMON_ERR, "获取当前登录的用户信息失败，请重新登录后重试!");
        }
        return currentUserLoginInfo;
    }

    @Override
    public UserDetail getCurrentUserDetails(UserLogin currentUserLogin)
    {
        UserDetail userDetail;
        String userId = currentUserLogin.getId();
        //获取缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(Constants.REDIS_KEY_USER_DETAIL_ID + userId);
        if (StrUtil.isNotBlank(jsonStr))
        {
            return JSONUtil.toBean(jsonStr, new TypeReference<UserDetail>() {
            }, true);
        }
        LambdaQueryWrapper<UserDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetail::getUserId, userId);

        try
        {
            userDetail = userDetailDao.selectOne(queryWrapper);
        }
        catch (Exception e)
        {
            throw new BusinessException(Code.COMMON_ERR, "获取当前登录的用户信息异常，请联系管理员处理!");
        }
        if (userDetail == null)
        {
            throw new BusinessException(Code.COMMON_ERR, "获取当前用户个人信息失败，请完善个人信息后重试!");
        }
        stringRedisTemplate.opsForValue().set(Constants.REDIS_KEY_USER_DETAIL_ID + userId, JSONUtil.toJsonStr(userDetail));
        return userDetail;
    }

    @Override
    public Result updatePassword(UserPasswordDTO userPasswordDTO)
    {
        String password = userPasswordDTO.getPassword();
        String newPassword = userPasswordDTO.getNewPassword();
        if (StrUtil.isBlank(password) || StrUtil.isBlank(newPassword))
        {
            throw new BusinessException(Code.COMMON_ERR, "密码或者新密码不能为空");
        }
        userPasswordDTO.setPassword(MD5Util.getMD5(password));
        userPasswordDTO.setNewPassword(MD5Util.getMD5(newPassword));
        int update = userLoginDao.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new BusinessException(Code.COMMON_ERR, "密码错误");
        }
        //更新缓存
        updateRedisCache(Constants.REDIS_KEY_ACCOUNT_ID , userPasswordDTO.getId(), null);
        return new Result(Code.COMMON_OK, null, "密码更新成功");
    }

    @Override
    public UserLogin getLoginById(String id)
    {
        return userLoginDao.selectById(id);
    }

    @Override
    public List<UserLogin> getAllUserLogin()
    {
        return userLoginDao.selectList(null);
    }

    @Override
    public UserLogin getLoginByUserName(String oldUsername)
    {
        LambdaUpdateWrapper<UserLogin> query = new LambdaUpdateWrapper<>();
        query.eq(StrUtil.isNotBlank(oldUsername), UserLogin::getUsername, oldUsername);
        return userLoginDao.selectOne(query);
    }

    /**
     * 获取角色的菜单
     * @param roleType 角色类型
     * @return 结果
     */
    private List<Menu> getRoleMenus(String roleType)
    {
        List<String> menuIds = roleMenuDao.selectByRoleCode(roleType);
        if (menuIds == null)
        {
            throw  new BusinessException(Code.BUSINESS_ERR, "当前用户角色无权限登录");
        }
        List<Menu> allMenus = menuService.findAll(null);
        List<Menu> resMenus = new ArrayList<>();
        for (Menu menu : allMenus)
        {
            if (menuIds.contains(menu.getId()))
            {
                resMenus.add(menu);
            }

            List<Menu> children = menu.getChildren();
            if (children != null)
            {
                children.removeIf(child -> !menuIds.contains(child.getId()));
            }
            menu.setChildren(children);
        }
        return resMenus;
    }

    /**
     * 更新redis缓存
     * @param keyPrefix key的前缀
     * @param relationId key的关联id
     * @param object 更新内容
     */
    void updateRedisCache(String keyPrefix, String relationId, Object object)
    {
        String key = keyPrefix + relationId;
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(jsonStr))
        {
            return;
        }
        if (object == null)
        {
           stringRedisTemplate.delete(key);
        }
        else
        {
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(object));
        }
    }

}
