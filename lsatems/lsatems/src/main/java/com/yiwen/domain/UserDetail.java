package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 用户详细信息表
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_user_detail")
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private Integer age;

    private String gender;

    @TableField(value = "avatar_path")
    private String avatarPath;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;

    private String hobby;

    private String email;

    private String school;

    private String college;

    private String major;

    private String tel;

    private String description;


}
