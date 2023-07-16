package com.yiwen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiwen.controller.dto.UserPasswordDTO;
import com.yiwen.domain.UserLogin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 存贮用户基本的登录信息 Mapper 接口
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
public interface UserLoginDao extends BaseMapper<UserLogin>
{

    @Update("update tbl_user_login set password = #{newPassword} where id = #{id} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);
}
