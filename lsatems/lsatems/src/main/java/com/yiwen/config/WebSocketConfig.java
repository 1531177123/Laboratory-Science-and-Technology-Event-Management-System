package com.yiwen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-23
 * @see WebSocketConfig
 **/

@Configuration
public class WebSocketConfig
{
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
