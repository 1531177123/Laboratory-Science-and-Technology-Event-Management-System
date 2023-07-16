package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.Team;
import com.yiwen.service.ITeamService;
import com.yiwen.service.IUserTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 实验室展示信息表 前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
@RestController
@RequestMapping("/team")
public class TeamController
{
    @Autowired
    private ITeamService teamService;

    @Autowired
    private IUserTeamService userTeamService;

    @PostMapping
    public Result save(@RequestBody Team team)
    {
        boolean flag = teamService.saveTeam(team);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增组队成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增组队失败");
    }

    @PutMapping
    public Result update(@RequestBody Team team)
    {
        boolean flag = teamService.updateTeamById(team);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新组队成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新组队失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = teamService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除组队成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除组队失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = teamService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除组队成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除组队失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @AuthAccess
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name, @RequestParam(required = false) String code
            , @RequestParam(required = false) String creator)
    {
        IPage<Team> teams = teamService.selectPage(currentPage, pageSize, name, code, creator);
        if (teams != null)
        {
            return new Result(Code.GET_OK, teams, "查询组队信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新组队信息失败");
        }
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPlayPage")
    public Result selectPlayPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name, @RequestParam(required = false) String code
            , @RequestParam(required = false) String creator, @RequestParam(required = false) String playId)
    {
        IPage<Team> teams = teamService.selectPlayPage(currentPage, pageSize, name, code, creator, playId);
        if (teams != null)
        {
            return new Result(Code.GET_OK, teams, "查询组队信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新组队信息失败");
        }
    }

    /**
     * 申请组队
     * @return 结果
     */
    @GetMapping("/applyTeam")
    public Result selectPage( @RequestParam String userId, @RequestParam String teamId, @RequestParam String content)
    {
        boolean saveFlag = userTeamService.saveById(userId, teamId, content);
        if (saveFlag)
        {
            return new Result(Code.GET_OK, null, "申请成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "申请失败");
        }
    }


    /**
     * 退出组队
     * @return 结果
     */
    @GetMapping("/quitTeam")
    public Result quitLab( @RequestParam String userId, @RequestParam String teamId)
    {
        boolean quitLabFlag = userTeamService.quitTeam(userId, teamId);
        if (quitLabFlag)
        {
            return new Result(Code.GET_OK, null, "退出成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "退出失败");
        }
    }

    /**
     * 查单条
     * @return 结果
     */
    @GetMapping("getById/{id}")
    public Result getById(@PathVariable String id)
    {
        Team team = teamService.getTeamById(id);
        if (team != null)
        {
            return new Result(Code.GET_OK, team, "查询成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询失败");
        }
    }

    /**
     * 查所有
     * @return 结果
     */
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) String userId, @RequestParam(required = false) String name
            , @RequestParam(required = false) String code)
    {
        List<Team> teams = teamService.getAll(userId, name, code);
        if (teams != null)
        {
            return new Result(Code.GET_OK, teams, "查询成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询失败");
        }
    }

    @PostMapping("/score")
    public Result certificationScore(@RequestParam("file") MultipartFile file, @RequestParam  String playId
                                    , @RequestParam  String teamId, @RequestParam  String score) throws Exception
    {
        boolean flag = teamService.certificationScore(file, playId, teamId, score);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "成绩认证申请成功");
        }
        return new Result(Code.SAVE_ERR, null, "成绩认证申请失败");
    }


}

