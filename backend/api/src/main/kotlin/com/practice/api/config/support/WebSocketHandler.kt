package com.practice.api.config.support

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketHandler: TextWebSocketHandler() {
    private val logger: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)
    companion object {
        private val webSocketSessionMap: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap()
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Connected Session: {}", session.id)

        webSocketSessionMap.put(session.id, session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("Connection Closed: {}, {}", session.id, status.reason)

        webSocketSessionMap.remove(session.id)
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info("{} send message: {}", session.id, message.payload)

        webSocketSessionMap.values
            .filter { it.uri == session.uri }
            .forEach { it.sendMessage(message) }
    }
}