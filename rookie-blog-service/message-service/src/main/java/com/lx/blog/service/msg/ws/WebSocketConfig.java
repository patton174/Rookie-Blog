package com.lx.blog.service.msg.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author LX
 * @date 2025/12/10
 * @description WebSocket 配置
 */
@Configuration
public class WebSocketConfig {

    /**
     * 扫描并注册所有 @ServerEndpoint 注解的 Bean
     * 注意：如果使用独立的 Servlet 容器（如 Tomcat），则不需要此 Bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
