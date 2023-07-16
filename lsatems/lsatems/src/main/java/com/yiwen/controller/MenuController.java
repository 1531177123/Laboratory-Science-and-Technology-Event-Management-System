package com.yiwen.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.Menu;
import com.yiwen.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-15
 */
@RestController
@RequestMapping("/menus")
public class MenuController
{
    @Autowired
    private IMenuService menuService;

    @PostMapping
    public Result save(@RequestBody Menu menu)
    {
        boolean flag = menuService.save(menu);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "添加菜单成功");
        }
        return new Result(Code.SAVE_ERR, null, "添加菜单失败");
    }

    @PutMapping
    public Result update(@RequestBody Menu menu)
    {
        boolean flag = menuService.updateById(menu);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新菜单成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新菜单失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = menuService.removeMenusById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除菜单成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除菜单失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = menuService.removeMenusByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除菜单成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除菜单失败");
    }


    @GetMapping
    public Result findAll(@RequestParam(required = false) String name)
    {
        List<Menu> menus = menuService.findAll(name);
        if (menus != null)
        {
            return new Result(Code.GET_OK, menus, "查询菜单信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新菜单信息失败");
        }
    }

    @GetMapping("/ids")
    public Result findAllIds()
    {
        List<String> menus = menuService.list().stream().map(Menu::getId).collect(Collectors.toList());
        return new Result(Code.GET_OK, menus, "查询菜单信息id成功");
    }
}

