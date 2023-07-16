package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

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
@TableName("tbl_team")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String code;

    @TableField(value = "limit_number")
    private String limitNumber;

    @TableField(value = "play_id")
    private String playId;

    @TableField(exist = false)
    private String playName;

    private String score;

    private String description;

    @TableField(value = "creator_id")
    private String creatorId;

    private String creator;

    private String status;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;

    @TableField(exist = false)
    private String personNumber;

    @TableField(exist = false)
    private List<String> partnerNames;

    @TableField(exist = false)
    private List<String> partnerIds;
}
