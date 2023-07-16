package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.dao.ArticleRemarkDao;
import com.yiwen.domain.ArticleRemark;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.IArticleRemarkService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 论坛文章评论表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-20
 */
@Service
public class ArticleRemarkServiceImpl extends ServiceImpl<ArticleRemarkDao, ArticleRemark> implements IArticleRemarkService {

    @Autowired
    private ArticleRemarkDao articleRemarkDao;

    @Autowired
    private IUserService userService;

    @Override
    public boolean saveArticleRemark(ArticleRemark articleRemark)
    {
        UserLogin currentUserLogin = userService.getCurrentUserLogin();
        articleRemark.setUserId(currentUserLogin.getId());
        articleRemark.setAvatarPath(userService.getCurrentUserDetails(currentUserLogin).getAvatarPath());
        articleRemark.setCreateTime(DateTimeUtil.getSysTime());
        if (articleRemark.getPid() != null)
        {
            String pid = articleRemark.getPid();
            ArticleRemark pArticleRemark = getById(pid);
            if (pArticleRemark.getOriginalId() != null)
            {
                articleRemark.setOriginalId(pArticleRemark.getOriginalId());
            }
            else{
                articleRemark.setOriginalId(articleRemark.getPid());
            }
        }
        return save(articleRemark);
    }

    @Override
    public boolean updateArticleRemarkById(ArticleRemark articleRemark)
    {
        articleRemark.setEditTime(DateTimeUtil.getSysTime());
        return updateById(articleRemark);
    }

    @Override
    public List<ArticleRemark> getTreeByArticleId(String articleId)
    {
        List<ArticleRemark> articleRemarks = articleRemarkDao.findRemarkDetails(articleId);
        List<ArticleRemark> originalList = articleRemarks.stream().filter(a -> a.getOriginalId() == null).collect(Collectors.toList());

        for (ArticleRemark original : originalList) {
            List<ArticleRemark> remarks = articleRemarks.stream().filter(remark -> original.getId().equals(remark.getOriginalId())).collect(Collectors.toList());
            remarks.forEach(articleRemark -> {
                articleRemarks.stream().filter(c1 -> c1.getId().equals(articleRemark.getPid())).findFirst().ifPresent(v -> {
                    articleRemark.setPUserId(v.getUserId());
                    articleRemark.setPName(v.getName());
                });
            });
            original.setChildren(remarks);
        }

        return originalList;
    }
}
