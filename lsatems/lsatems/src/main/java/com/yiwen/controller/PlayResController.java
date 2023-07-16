package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.Files;
import com.yiwen.domain.PlayRemark;
import com.yiwen.domain.PlayRes;
import com.yiwen.service.IPlayResService;
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
 * @since 2023-03-25
 */
@RestController
@RequestMapping("/playRes")
public class PlayResController
{
    @Autowired
    private IPlayResService playResService;

    @PostMapping("/save")
    public Result save(@RequestParam("file") MultipartFile file, @RequestParam  String playId) throws Exception
    {
        boolean flag = playResService.savePlayRes(file, playId);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "新增赛事资源成功");
        }
        return new Result(Code.SAVE_ERR, null, "新增赛事资源失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = playResService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除赛事资源成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除赛事资源失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = playResService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除赛事资源成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除赛事资源失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(value = "playId", required = false) String playId)
    {
        IPage<PlayRes> playResIPages = playResService.selectPage(currentPage, pageSize, playId);
        if (playResIPages != null)
        {
            return new Result(Code.GET_OK, playResIPages, "查询赛事资源成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询赛事资源失败");
        }
    }

}

