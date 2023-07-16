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
 * 科技赛事详细信息表
 * </p>
 *
 * @author yiwen
 * @since 2023-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_play")
public class Play implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String priority;

    private String description;

    @TableField(value = "lab_id")
    private String labId;

    @TableField(exist = false)
    private String labName;

    @TableField(value = "creator_id")
    private String creatorId;

    private String creator;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;


}
