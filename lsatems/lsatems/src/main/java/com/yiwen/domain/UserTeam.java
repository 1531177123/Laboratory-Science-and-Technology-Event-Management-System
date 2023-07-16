package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 实验室展示信息表
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_user_team")
public class UserTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "team_id")
    private String teamId;

    private String role;

    private String status;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;


}
