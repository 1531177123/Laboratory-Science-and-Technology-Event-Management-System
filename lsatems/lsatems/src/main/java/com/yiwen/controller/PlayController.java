package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.Lab;
import com.yiwen.domain.Play;
import com.yiwen.service.IPlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 科技赛事详细信息表 前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-24
 */
@RestController
@RequestMapping("/play")
public class PlayController
{

    @Autowired
    private IPlayService playService;

    @PostMapping
    public Result save(@RequestBody Play play)
    {
        boolean flag = playService.savePlay(play);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "添加科技赛事成功");
        }
        return new Result(Code.SAVE_ERR, null, "添加科技赛事失败");
    }

    @PutMapping
    public Result update(@RequestBody Play play)
    {
        boolean flag = playService.updatePlayById(play);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新科技赛事成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新科技赛事失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = playService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除科技赛事信息成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除科技赛事信息失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = playService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除科技赛事成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除科技赛事失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(value = "name", required = false) String name, @RequestParam(value = "labId", required = false) String labId
            , @RequestParam(value = "priority", required = false) String priority)
    {
        IPage<Play> plays = playService.selectPage(currentPage, pageSize, name, labId, priority);
        if (plays != null)
        {
            return new Result(Code.GET_OK, plays, "查询科技赛事信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新科技赛事信息失败");
        }
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable String id)
    {
        Play play = playService.getById(id);
        if (play != null)
        {
            return new Result(Code.GET_OK, play, "查询科技赛事信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新科技赛事信息失败");
        }

    }
}

