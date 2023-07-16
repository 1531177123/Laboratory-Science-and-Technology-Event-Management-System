package com.yiwen.controller;


import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.controller.dto.UserLoginDTO;
import com.yiwen.controller.dto.UserPasswordDTO;
import com.yiwen.service.IUserService;
import com.yiwen.utils.CheckCodeUtil;
import com.yiwen.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 * 用户登录控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@RestController
@RequestMapping("/login")
public class LoginController
{
    @Autowired
    private IUserService userService;

    private String verificationCode;

    @GetMapping("/generateVerificationCode")
    public Result generateVerificationCode(HttpServletResponse response) throws Exception
    {
        // 生成验证码
        ServletOutputStream os = response.getOutputStream();
        String checkCode = CheckCodeUtil.outputVerifyImage(100, 50, os, 4);
        verificationCode = checkCode;
        return new Result(Code.COMMON_OK, null);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Map<String,String> userInfo, HttpServletRequest request)
    {
        if (!userInfo.get("checkCode").equalsIgnoreCase(verificationCode))
        {
            return new Result(Code.COMMON_ERR, null, "验证码输入错误");
        }
        Boolean registerFlag = userService.register(userInfo);

        if (registerFlag)
        {
            return new Result(Code.COMMON_OK, null, "注册成功,3秒后自动跳回登录页……");
        }
        else
        {
            return new Result(Code.COMMON_ERR, null, "注册失败");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO loginInfo)
    {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password))
        {
            return new Result(Code.COMMON_ERR, null, "账号和密码都不能为空!");
        }
        UserLoginDTO login = userService.login(loginInfo);

        return new Result(Code.COMMON_OK, login, "登录成功");
    }


    @PostMapping("/findBack")
    public Result findBack(@RequestBody Map<String, String> findMap)
    {
        String name = findMap.get("name");
        String email = findMap.get("email");
        String tel = findMap.get("tel");
        String newPass = findMap.get("newPass");
        if (name == null || email == null || tel == null || newPass == null)
        {
            new Result(Code.COMMON_ERR, null, "找回密码的信息不完整！请填写完整后重试");
        }
        return userService.findBack(name, email, tel, newPass);
    }

    @PostMapping("/password")
    public Result modifyPassword(@RequestBody UserPasswordDTO userPasswordDTO)
    {
        return userService.updatePassword(userPasswordDTO);
    }


}

