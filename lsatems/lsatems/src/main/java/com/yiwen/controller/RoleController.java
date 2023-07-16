package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.Role;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.IRoleService;
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
 * @since 2023-03-14
 */
@RestController
@RequestMapping("/roles")
public class RoleController
{
    @Autowired
    private IRoleService roleService;

    @PostMapping
    public Result save(@RequestBody Role role)
    {
        boolean flag = roleService.saveRole(role);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "添加角色成功");
        }
        return new Result(Code.SAVE_ERR, null, "添加角色失败");
    }

    @PutMapping
    public Result update(@RequestBody Role role)
    {
        boolean flag = roleService.updateRoleById(role);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新角色成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新角色失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = roleService.removeRoleById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除角色成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除角色失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = roleService.removeRolesByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除角色成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除角色失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name)
    {
        IPage<Role> roles = roleService.selectPage(currentPage, pageSize, name);
        if (roles != null)
        {
            return new Result(Code.GET_OK, roles, "查询角色信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新角色信息失败");
        }
    }

    @PostMapping("/roleMenu/{roleCode}")
    public Result roleMenu(@PathVariable String roleCode, @RequestBody List<String> menuIds)
    {
        roleService.setRoleMenu(roleCode, menuIds);
        return new Result(Code.COMMON_OK, null, "更新角色菜单关系成功");
    }

    @GetMapping("/roleMenu/{roleCode}")
    public Result getRoleMenu(@PathVariable String roleCode)
    {
        List<String> menuIds = roleService.getRoleMenu(roleCode);
        return new Result(Code.COMMON_OK, menuIds, "获取角色菜单成功");
    }

    @GetMapping
    public Result findAllRoles()
    {
        List<Role> roles = roleService.list();
        return new Result(Code.GET_OK, roles, "获取所有角色信息成功");
    }
}

