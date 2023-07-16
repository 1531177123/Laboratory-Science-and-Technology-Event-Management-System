package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 存贮用户基本的登录信息
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_user_login")
@ToString
public class UserLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String username;

//    @JsonIgnore
    private String password;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;

    @TableField(value = "last_login_time")
    private String lastLoginTime;

    @TableField(value = "role_type")
    private String roleType;

    @TableField(value = "detail_flag")
    private String detailFlag;

    @TableLogic
    private String deleted;


}
