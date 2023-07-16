package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Result;
import com.yiwen.controller.dto.UserLoginDTO;
import com.yiwen.controller.dto.UserPasswordDTO;
import com.yiwen.domain.UserDetail;
import com.yiwen.domain.UserLogin;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 论坛文章信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@Transactional
public interface IUserService
{
    /**
     * 登录
     * @return 结果
     */
    UserLoginDTO login(UserLoginDTO userLoginDTO);
    /**
     * 注册
     * @param userInfo 用户信息
     * @return 结果
     */
    Boolean register(Map<String, String> userInfo);

    /**
     * 新增
     * @param userInfo 用户信息
     * @return 结果
     */
    Result save(UserLogin userInfo);

    /**
     * 更新
     * @param userInfo 用户信息
     * @return 结果
     */
    Result update(UserLogin userInfo);

    /**
     * 删除用户信息
     * @param id id集合
     * @return 结果
     */
    Boolean delete(String id);

    /**
     * 根据id查询
     * @param id
     * @return 结果
     */
    UserLogin selectById(String id);

    /**
     * 分页查询
     * @return 结果
     */
    IPage<UserLogin> selectPage(long currentPage, long pageSize, String roleType, String username, String deleted);

    /**
     * 批量删除
     * @param ids
     * @return 结果
     */
    Boolean deleteBatchByIds(List<String> ids);

    /**
     * 查询所有用户信息
     * @return 结果
     */
    List<UserLogin> selectAll();

    /**
     * 导入用户信息
     * @param userInfo 用户信息
     * @return 结果
     */
    Result importUserLoginInfo(List<UserLogin> userInfo);

    /**
     * 保存用户详细信息
     * @param userInfo 用户信息
     * @return 结果
     */
    Result saveDetails(UserDetail userInfo);

    /**
     * 密码找回
     * @param name 姓名
     * @param email email
     * @param tel  电话
     * @return 结果
     */
    Result findBack(String name, String email, String tel, String newPass);

    /**
     * 获得用户展示的信息
     * @return 结果
     */
    Result getShowInfo();

    /**
     * 获取当前用户详细信息
     * @return 结果
     */
    Result getDetails();

    /**
     * 更新当前用户详细信息
     * @return 结果
     */
    Result updateDetails(UserDetail userDetail);

    /**
     * 获得当前账号信息
     * @return 结果
     */
    UserLogin getCurrentUserLogin();

    /**
     * 获取当前用户详细信息
     * @return 结果
     */
    UserDetail getCurrentUserDetails(UserLogin currentUserLogin);

    /**
     * 修改密码
     * @param userPasswordDTO 修改密码数据
     * @return 结果
     */
    Result updatePassword(UserPasswordDTO userPasswordDTO);

    /**
     * 通过id查询账号
     * @param id id
     * @return 结果
     */
    UserLogin getLoginById(String id);

    /**
     * 获取所有的账号信息
     * @return 结果
     */
    List<UserLogin> getAllUserLogin();

    /**
     * 通过用户名查账号
     * @param oldUsername 用户名
     * @return 结果
     */
    UserLogin getLoginByUserName(String oldUsername);
}
