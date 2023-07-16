package com.yiwen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.ArticleRemark;

import java.util.List;

/**
 * <p>
 * 论坛文章评论表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-20
 */
public interface IArticleRemarkService extends IService<ArticleRemark> {

    /**
     * 保存评论
     * @param articleRemark 评论
     * @return 结果
     */
    boolean saveArticleRemark(ArticleRemark articleRemark);

    /**
     * 更新评论
     * @param articleRemark 评论
     * @return 结果
     */
    boolean updateArticleRemarkById(ArticleRemark articleRemark);

    /**
     * 根据文章查询所有评论
     * @param articleId 文章id
     * @return 结果
     */
    List<ArticleRemark> getTreeByArticleId(String articleId);
}
