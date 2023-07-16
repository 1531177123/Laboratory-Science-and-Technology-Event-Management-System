package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.yiwen.domain.Files;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_team_res")
public class TeamRes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField(value = "team_id")
    private String teamId;

    @TableField(exist = false)
    private String teamName;

    @TableField(value = "file_id")
    private String fileId;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(exist = false)
    private Files resource;

}
