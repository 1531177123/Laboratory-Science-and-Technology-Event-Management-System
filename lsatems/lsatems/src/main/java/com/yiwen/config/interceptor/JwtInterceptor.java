package com.yiwen.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.yiwen.common.Code;
import com.yiwen.config.AuthAccess;
import com.yiwen.domain.UserLogin;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-11
 * @see JwtInterceptor
 **/
public class JwtInterceptor implements HandlerInterceptor
{


    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String token = request.getHeader("token");
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }else{
            //放行特殊方法注释
            HandlerMethod h = (HandlerMethod)handler;
            AuthAccess methodAnnotation = h.getMethodAnnotation(AuthAccess.class);
            if (methodAnnotation != null)
            {
                return true;
            }
        }

        //开始认证token
        if (StrUtil.isBlank(token))
        {
            throw new BusinessException(Code.TOKEN_ERR, "无token，请重新登录");
        }

        //验证
        String userId;
        try
        {

            userId = JWT.decode(token).getAudience().get(0);
        }
        catch ( JWTDecodeException j)
        {
            throw new BusinessException(Code.TOKEN_ERR, "token验证失败");
        }
        UserLogin userLogin = userService.selectById(userId);
        if (userLogin == null)
        {
            throw new BusinessException(Code.TOKEN_ERR, "用户不存在,请重新登录");
        }

        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(userLogin.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new BusinessException(Code.TOKEN_ERR, "token验证失败");
        }
        return true;

    }
}
