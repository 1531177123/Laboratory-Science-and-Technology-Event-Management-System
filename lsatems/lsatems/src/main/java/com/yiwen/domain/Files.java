package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-12
 * @see Files
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_file")
public class Files
{
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String type;

    private String size;

    private String md5;

    private String url;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;

    @TableLogic
    private String deleted;

    private String enable;

    @TableField(value = "creator_id")
    private String creatorId;

    @TableField(exist = false)
    private String creator;

}
