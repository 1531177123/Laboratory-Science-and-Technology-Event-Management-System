package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.Dict;
import com.yiwen.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-15
 */
@RestController
@RequestMapping("/dict")
public class DictController
{
    @Autowired
    private IDictService dictService;

    @PostMapping
    public Result save(@RequestBody Dict dict)
    {
        boolean flag = dictService.save(dict);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "添加字典成功");
        }
        return new Result(Code.SAVE_ERR, null, "添加字典失败");
    }

    @PutMapping
    public Result update(@RequestBody Dict dict)
    {
        boolean flag = dictService.updateById(dict);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新字典成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新字典失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = dictService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除字典信息成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除字典信息失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = dictService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除字典成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除字典失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name, @RequestParam(required = false) String type)
    {
        IPage<Dict> dicts = dictService.selectPage(currentPage, pageSize, name, type);
        if (dicts != null)
        {
            return new Result(Code.GET_OK, dicts, "查询字典信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新字典信息失败");
        }
    }

    @AuthAccess
    @GetMapping("/getByType")
    public Result getDictByType(@RequestParam String type)
    {
        List<Dict> dictByType = dictService.getDictByType(type);
        if (dictByType != null)
        {
            return new Result(Code.GET_OK, dictByType, "查询成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询失败");
        }
    }


}

