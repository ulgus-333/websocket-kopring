package com.practice.socket.config

import com.practice.socket.config.support.WebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class WebSocketConfig (
    private val handler: WebSocketHandler
): WebSocketConfigurer {
    companion object {
        const val SESSION_URI: String = "/ws/chats/{roomId}"
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, SESSION_URI)
            .setAllowedOrigins("*")
    }
}