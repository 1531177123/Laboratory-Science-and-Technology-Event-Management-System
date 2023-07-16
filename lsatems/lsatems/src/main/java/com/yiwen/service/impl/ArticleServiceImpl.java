package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.common.Code;
import com.yiwen.common.Constants;
import com.yiwen.dao.ArticleDao;
import com.yiwen.dao.UserDetailDao;
import com.yiwen.domain.Article;
import com.yiwen.domain.Role;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.IArticleService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import com.yiwen.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 论坛文章信息表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-17
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements IArticleService
{

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private IUserService userService;

    @Override
    public IPage<Article> selectPage(long currentPage, long pageSize, String title, String type, String creator)
    {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(title), Article::getName, title);
        queryWrapper.eq(StrUtil.isNotBlank(type), Article::getType, type);
        queryWrapper.like(StrUtil.isNotBlank(creator), Article::getCreator, creator);
        queryWrapper.orderByDesc(Article::getHot);
        return articleDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
    }

    @Override
    public boolean saveArticle(Article article)
    {
        article.setCreateTime(DateTimeUtil.getSysTime());
        String creator = userService.getCurrentUserDetails(userService.getCurrentUserLogin()).getName();
        article.setCreator(creator);
        article.setHot(1);
        return save(article);
    }

    @Override
    public boolean updateArticleById(Article article)
    {
        article.setEditTime(DateTimeUtil.getSysTime());
        Integer hot = article.getHot();
        if (hot.equals(0))
        {
            article.setHot(Integer.MAX_VALUE);
        }
        return updateById(article);
    }

    @Override
    public Article getArticleById(String id)
    {
        Article article = getById(id);
        Integer hot = article.getHot();
        if (hot.equals(Integer.MAX_VALUE))
        {
            return article;
        }
        article.setHot(hot + 1);
        if (updateById(article))
        {
            return article;
        }
        throw new BusinessException(Code.BUSINESS_ERR, "获取文章信息出错");
    }
}
