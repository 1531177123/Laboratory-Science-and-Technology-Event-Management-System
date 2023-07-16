package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-15
 * @see RoleMenu
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_role_menu")
public class RoleMenu implements Serializable
{
    private static final long serialVersionUID = 1L;

    @TableField(value = "menu_id")
    private String menuId;

    @TableField(value = "role_code")
    private String roleCode;
}
