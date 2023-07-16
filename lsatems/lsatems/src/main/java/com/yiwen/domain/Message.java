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
 * 
 * </p>
 *
 * @author yiwen
 * @since 2023-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String type;

    @TableField(value = "relation_id")
    private String relationId;

    private String content;

    @TableField(value = "to_user")
    private String toUser;

    @TableField(value = "res_path")
    private String resPath;

    private String creator;

    @TableField(value = "creator_id")
    private String creatorId;

    private String status;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(exist = false)
    private List<String> toUserIds;

}
