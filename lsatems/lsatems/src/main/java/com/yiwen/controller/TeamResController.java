package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.PlayRes;
import com.yiwen.domain.TeamRes;
import com.yiwen.service.IPlayResService;
import com.yiwen.service.ITeamResService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
@RestController
@RequestMapping("/teamRes")
public class TeamResController
{
    @Autowired
    private ITeamResService teamResService;

    @PostMapping("/save")
    public Result save(@RequestParam("file") MultipartFile file, @RequestParam  String teamId) throws Exception
    {
        boolean flag = teamResService.savePlayRes(file, teamId);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增组队资源成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增组队资源失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = teamResService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除组队资源成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除组队资源失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = teamResService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除组队资源成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除组队资源失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(value = "teamId", required = false) String teamId)
    {
        IPage<TeamRes> teamResIPages = teamResService.selectPage(currentPage, pageSize, teamId);
        if (teamResIPages != null)
        {
            return new Result(Code.GET_OK, teamResIPages, "查询组队资源成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询组队资源失败");
        }
    }


}

