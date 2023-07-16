package com.yiwen.controller;


import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.PlayRemark;
import com.yiwen.domain.TeamRemark;
import com.yiwen.service.IPlayRemarkService;
import com.yiwen.service.ITeamRemarkService;
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
@RequestMapping("/teamRemarks")
public class TeamRemarkController
{
    @Autowired
    private ITeamRemarkService teamRemarkService;

    @PostMapping
    public Result save(@RequestBody TeamRemark teamRemark)
    {
        boolean flag = teamRemarkService.saveTeamRemark(teamRemark);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增评论成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增评论失败");
    }

    @PutMapping
    public Result update(@RequestBody TeamRemark teamRemark)
    {
        boolean flag = teamRemarkService.updateTeamRemarkById(teamRemark);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新评论成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新评论失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = teamRemarkService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除评论成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除评论失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = teamRemarkService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除评论成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除评论失败");
    }


    @GetMapping("/{id}")
    public Result findPlayRemarkById(@PathVariable String id)
    {
        TeamRemark playRemark = teamRemarkService.getById(id);
        return new Result(Code.GET_OK, playRemark, "获取评论信息成功");
    }

    @AuthAccess
    @GetMapping("/tree/{teamId}")
    public Result findTree(@PathVariable String teamId)
    {
        List<TeamRemark> playRemarks = teamRemarkService.getTreeByTeamId(teamId);
        return new Result(Code.GET_OK, playRemarks, "查询所有评论信息成功");
    }

}

