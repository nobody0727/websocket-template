package com.example.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 * 配置消息代理和STOMP端点
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    /**
     * 配置消息代理
     * - /topic: 用于广播消息
     * - /user: 用于用户私有消息
     * - /app: 应用目标前缀
     * - /user: 用户目标前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单消息代理，用于广播和用户私有消息
        config.enableSimpleBroker("/topic", "/user");
        // 设置应用目标前缀，客户端发送消息时使用
        config.setApplicationDestinationPrefixes("/app");
        // 设置用户目标前缀，用于用户私有消息
        config.setUserDestinationPrefix("/user");
    }
    
    /**
     * 注册STOMP端点
     * 客户端通过 /ws 端点连接WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}