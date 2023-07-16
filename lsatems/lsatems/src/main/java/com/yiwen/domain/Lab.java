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
 * 实验室详细信息表
 * </p>
 *
 * @author yiwen
 * @since 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_lab")
public class Lab implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String school;

    private String college;

    private String type;

    private String code;

    @TableField(exist = false)
    private String personNumber;

    @TableField(value = "avatar_path")
    private String avatarPath;

    private String description;

    @TableField(value = "creator_id")
    private String creatorId;

    private String creator;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;

    @TableField(exist = false)
    private List<Play> plays;

    @TableField(exist = false)
    private List<String> memberNames;

    @TableField(exist = false)
    private String memberNamesStr;
}
