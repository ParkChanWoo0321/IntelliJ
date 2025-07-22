package com.example.hackerthon.Notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 브로커 접두사
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트 요청 접두사
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트에서 연결할 endpoint, SockJS fallback 지원
        registry.addEndpoint("/ws-notify").setAllowedOriginPatterns("*");//.withSockJS();
    }
}
