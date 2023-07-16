package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.Article;

/**
 * <p>
 * 论坛文章信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-17
 */
public interface IArticleService extends IService<Article> {

    /**
     * 一句话描述功能
     * @param currentPage 当前页码
     * @param pageSize 每页数据量
     * @param title 标题
     * @param creator  作者
     * @return 结果
     */
    IPage<Article> selectPage(long currentPage, long pageSize, String title, String type, String creator);

    /**
     * 新增
     * @param article
     * @return 结果
     */
    boolean saveArticle(Article article);

    /**
     * 更新
     * @param article
     * @return 结果
     */
    boolean updateArticleById(Article article);

    /**
     * 根据id查询文章
     * @param id
     * @return 结果
     */
    Article getArticleById(String id);
}
