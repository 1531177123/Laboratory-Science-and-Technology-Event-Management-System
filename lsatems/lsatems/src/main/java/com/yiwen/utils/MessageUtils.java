package com.yiwen.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yiwen.service.IUserService;
import com.yiwen.service.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-23
 * @see MessageUtils
 **/
@Component
public class MessageUtils
{
    @Autowired
    private WebSocketServer webSocketServer;

    private static WebSocketServer staticWebSocketServer;


    @PostConstruct
    public void setWebSocketServer()
    {
        staticWebSocketServer = webSocketServer;
    }


    public static void sendMessageToUsers(String type, String title, String msg, String to, List<String> toIds)
    {
        String message = getJsonMsg(type, title, to, msg);
        staticWebSocketServer.sendInfoToUsers(message, toIds);
    }

    public static void sendMessageToUser(String type, String title, String msg, String to, String userId)
    {
        String message = getJsonMsg(type, title, to, msg);
        staticWebSocketServer.sendInfoToUser(message, userId);
    }
    private static String getJsonMsg(String type, String title, String to, String msg)
    {
        Map<String, String> msgMp = new HashMap<>();
        msgMp.put("type", type);
        msgMp.put("title", title);
        msgMp.put("routerTo", to);
        msgMp.put("msg", msg);
        return JSONUtil.toJsonPrettyStr(msgMp);
    }
}
