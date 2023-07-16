package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.Article;
import com.yiwen.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 论坛文章信息表 前端控制器
 * </article
 * @author yiwen
 * @siarticle2023-03-06
 */
@RestController
@RequestMapping("/articles")
public class ArticleController
{
    @Autowired
    private IArticleService articleService;

    @PostMapping
    public Result save(@RequestBody Article article)
    {
        boolean flag = articleService.saveArticle(article);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增文章成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增文章失败");
    }

    @PutMapping
    public Result update(@RequestBody Article article)
    {
        boolean flag = articleService.updateArticleById(article);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新文章成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新文章失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = articleService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除文章成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除文章失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = articleService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除文章成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除文章失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @AuthAccess
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name, @RequestParam(required = false) String type, @RequestParam(required = false) String creator)
    {
        IPage<Article> articles = articleService.selectPage(currentPage, pageSize, name, type, creator);
        if (articles != null)
        {
            return new Result(Code.GET_OK, articles, "查询文章信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新文章信息失败");
        }
    }


    @GetMapping
    public Result findAll()
    {
        List<Article> articles = articleService.list();
        return new Result(Code.GET_OK, articles, "获取所有文章信息成功");
    }

    @AuthAccess
    @GetMapping("/{id}")
    public Result findArticleById(@PathVariable String id)
    {
        Article article = articleService.getArticleById(id);
        return new Result(Code.GET_OK, article, "获取文章信息成功");
    }

}

