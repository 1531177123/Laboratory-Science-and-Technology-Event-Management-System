package com.yiwen.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 论坛文章评论表
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_article_remark")
public class ArticleRemark implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String content;

    @TableField(value = "article_id")
    private String articleId;

    private String pid;

    @TableField(exist = false)
    private String pName;

    @TableField(exist = false)
    private String pUserId;

    @TableField(value = "original_id")
    private String originalId;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "edit_time")
    private String editTime;

    @TableField(exist = false)
    private String name;

    @TableField(value = "avatar_path", exist = false)
    private String avatarPath;

    @TableField(exist = false)
    private List<ArticleRemark> children;


}
