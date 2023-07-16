package com.yiwen.controller;


import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.PlayRemark;
import com.yiwen.service.IPlayRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 科技赛事评论表 前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
@RestController
@RequestMapping("/playRemarks")
public class PlayRemarkController
{
    @Autowired
    private IPlayRemarkService playRemarkService;

    @PostMapping
    public Result save(@RequestBody PlayRemark playRemark)
    {
        boolean flag = playRemarkService.savePlayRemark(playRemark);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增评论成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增评论失败");
    }

    @PutMapping
    public Result update(@RequestBody PlayRemark playRemark)
    {
        boolean flag = playRemarkService.updatePlayRemarkById(playRemark);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新评论成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新评论失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = playRemarkService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除评论成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除评论失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = playRemarkService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除评论成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除评论失败");
    }


    @GetMapping("/{id}")
    public Result findPlayRemarkById(@PathVariable String id)
    {
        PlayRemark playRemark = playRemarkService.getById(id);
        return new Result(Code.GET_OK, playRemark, "获取评论信息成功");
    }

    @AuthAccess
    @GetMapping("/tree/{playId}")
    public Result findTree(@PathVariable String playId)
    {
        List<PlayRemark> playRemarks = playRemarkService.getTreeByPlayId(playId);
        return new Result(Code.GET_OK, playRemarks, "查询所有评论信息成功");
    }

}

