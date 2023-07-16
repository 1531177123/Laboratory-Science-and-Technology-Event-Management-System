package com.yiwen.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.IUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-11
 * @see TokenUtils
 **/
@Component
public class TokenUtils
{

    private static IUserService staticUserService;

    @Resource
    private IUserService userService;

    @PostConstruct
    public void setUserService()
    {
        staticUserService = userService;
    }

    /**
     * 生成token
     * @param userId 用户id
     * @param sign 密码
     * @return 结果
     */
    public static String getToken(String userId, String sign)
    {
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) //2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前的用户
     * @return 结果
     */
    public static UserLogin getCurrentUserLoginInfo()
    {
        try
        {
            HttpServletRequest request  = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token))
            {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.selectById(userId);
            }
        }
        catch (Exception e)
        {
            return null;
        }
      return null;
    }

}
