package com.yiwen.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-23
 * @see WebSocketServer
 **/
@Component
@ServerEndpoint("/webSocket/{userId}")
@Slf4j
public class WebSocketServer
{

    //存放客户端对应的WebSocket对象--key:userId, value:websocket对象
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    //与客户端的连接会话
    private Session session;

    //用户id
    private String userId = "";


    //连接成功调用的方法
 /*   @OnOpen
    public void onOpen( Session session)
    {
        this.session = session;
        sendMessage("连接成功");
    }*/

    //连接成功调用的方法
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session)
    {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId))
        {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        }
        else
        {
            webSocketMap.put(userId, this);
        }
    }

    //接收客户端的消息
    @OnMessage
    public void onMessage(String message, Session session)
    {
        log.info("用户消息：" + userId + ",报文" + message);
        if (StrUtil.isNotBlank(message))
        {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSONUtil.parseObj(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getStr("toUserId");
                //传送给对应toUserId用户的websocket
                if (StrUtil.isNotBlank(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(message);
                } else {
                    //否则不在这个服务器上，发送到mysql或者redis
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //错误处理
    @OnError
    public void onError(Session session, Throwable error){
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    //推送消息
    public void sendMessage(String message){
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送自定
     * 义消息
     **/
    public void sendInfoToUser(String message, String userId) {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StrUtil.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    /**
     * 发送自定
     * 义消息
     **/
    public void sendInfoToUsers(String message, List<String> userIds) {
        for (String id : userIds)
        {
            if (StrUtil.isNotBlank(id) && webSocketMap.containsKey(id)) {
                log.info("发送消息到:" + id + "，报文:" + message);
                webSocketMap.get(id).sendMessage(message);
            } else {
                log.error("用户" + id + ",不在线！");
            }
        }

    }

    //连接关闭处理
    @OnClose
    public void onClose(){
        if (webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
        }
        log.info("用户退出:" + userId );
    }

}
