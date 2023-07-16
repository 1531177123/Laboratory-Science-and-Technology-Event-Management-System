package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.ArticleRemark;
import com.yiwen.service.IArticleRemarkService;
import com.yiwen.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 论坛评论评论表 前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@RestController
@RequestMapping("/articleRemarks")
public class ArticleRemarkController
{
    @Autowired
    private IArticleRemarkService articleRemarkService;

    @PostMapping
    public Result save(@RequestBody ArticleRemark articleRemark)
    {
        boolean flag = articleRemarkService.saveArticleRemark(articleRemark);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增评论成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增评论失败");
    }

    @PutMapping
    public Result update(@RequestBody ArticleRemark articleRemark)
    {
        boolean flag = articleRemarkService.updateArticleRemarkById(articleRemark);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新评论成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新评论失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = articleRemarkService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除评论成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除评论失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = articleRemarkService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除评论成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除评论失败");
    }


    @GetMapping("/{id}")
    public Result findArticleById(@PathVariable String id)
    {
        ArticleRemark articleRemark = articleRemarkService.getById(id);
        return new Result(Code.GET_OK, articleRemark, "获取所有评论信息成功");
    }

    @AuthAccess
    @GetMapping("/tree/{articleId}")
    public Result findTree(@PathVariable String articleId)
    {
        List<ArticleRemark> articleRemarks = articleRemarkService.getTreeByArticleId(articleId);
        return new Result(Code.GET_OK, articleRemarks, "查询所有评论信息成功");
    }


}

