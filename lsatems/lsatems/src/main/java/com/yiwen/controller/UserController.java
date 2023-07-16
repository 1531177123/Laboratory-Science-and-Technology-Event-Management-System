package com.yiwen.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.UserDetail;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-08
 * @see UserController
 **/
@RestController
@RequestMapping("/users")
public class UserController
{
    @Autowired
    private UserServiceImpl userLogin;

    /**
     * 新增
     * @param userInfo 用户信息
     * @return 结果
     */
    @PostMapping
    public Result save(@RequestBody UserLogin userInfo)
    {
        return userLogin.save(userInfo);
    }

    /**
     * 新增用户详细信息
     * @param userInfo 用户信息
     * @return 结果
     */
    @PostMapping("/details")
    public Result saveDetails(@RequestBody UserDetail userInfo)
    {
        return userLogin.saveDetails(userInfo);
    }

    /**
     * 更新
     * @param userInfo 用户信息
     * @return 结果
     */
    @PutMapping
    public Result update(@RequestBody UserLogin userInfo)
    {
        return userLogin.update(userInfo);
    }

    /**
     * 删除用户信息
     * @param id id
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable String id)
    {
        Boolean flag = userLogin.delete(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除用户成功");
        }
        else
        {
            return new Result(Code.DELETE_ERR, null, "删除用户失败");
        }
    }

    /**
     * 批量删除用户信息
     * @param ids ids
     * @return 结果
     */
    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        Boolean flag = userLogin.deleteBatchByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除用户成功");
        }
        else
        {
            return new Result(Code.DELETE_ERR, null, "批量删除用户失败");
        }
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
                            , @RequestParam(required = false) String roleType, @RequestParam(required = false) String username, @RequestParam(required = false) String detailFlag)
    {
        IPage<UserLogin> userLogins = userLogin.selectPage(currentPage, pageSize, roleType, username, detailFlag);
        if (userLogins != null)
        {
            return new Result(Code.GET_OK, userLogins, "查询用户信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新用户信息失败");
        }
    }

    /**
     * 根据id查询
     * @param id
     * @return 结果
     */
    @GetMapping("/{id}")
    public Result selectById(@PathVariable("id") String id)
    {
        UserLogin userInfo = userLogin.selectById(id);
        if (userInfo != null)
        {
            return new Result(Code.GET_OK, userInfo, "查询用户信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新用户信息失败");
        }
    }

    /**
     * 导出接口
     * @param response
     * @return 结果
     */
    @AuthAccess
    @GetMapping("/export")
    public void export(HttpServletResponse response)throws Exception
    {
        List<UserLogin> userInfo = userLogin.selectAll();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("editTime", "修改时间");
        writer.addHeaderAlias("lastLoginTime", "上一次登录时间");
        writer.addHeaderAlias("roleType", "角色(0:学生,1:老师,2:管理员)");
        writer.addHeaderAlias("detailFlag", "初始化信息(0:未完成,1:已完成)");
        writer.setOnlyAlias(true);
        writer.write(userInfo, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户账号信息", "UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+ fileName +".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 导入用户账号信息
     * @return 结果
     */
    @AuthAccess
    @PostMapping("/import")
    public Result importUserLoginInfo(MultipartFile file) throws Exception
    {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("用户名","username");
        reader.addHeaderAlias("密码", "password");
        reader.addHeaderAlias("角色(0:学生,1:老师,2:管理员)", "roleType");
        reader.setIgnoreEmptyRow(true);
        List<UserLogin> userLogins = reader.readAll(UserLogin.class);
        return userLogin.importUserLoginInfo(userLogins);
    }

    @GetMapping("/getShowInfo")
    public Result getShowInfo()
    {
        return userLogin.getShowInfo();
    }

    @GetMapping("/getDetails")
    public Result getDetails()
    {
        return userLogin.getDetails();
    }

    @PutMapping("/updateDetails")
    public Result updateDetails(@RequestBody UserDetail userDetail)
    {
        return userLogin.updateDetails(userDetail);
    }
}
