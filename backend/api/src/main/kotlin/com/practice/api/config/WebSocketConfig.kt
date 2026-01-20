package com.practice.api.config

import com.practice.api.config.support.WebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class WebSocketConfig(
    private val handler: WebSocketHandler
): WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, "/ws/chats/{roomId}")
    }
}