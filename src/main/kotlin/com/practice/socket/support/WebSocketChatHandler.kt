package com.practice.socket.support

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketChatHandler: TextWebSocketHandler() {
    private val webSocketSessionMap: MutableMap<String, WebSocketSession> = ConcurrentHashMap()
    private val log: Logger = LoggerFactory.getLogger(WebSocketChatHandler::class.java)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("{} connected", session.id)
        webSocketSessionMap.put(session.id, session)
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        log.info("{} send {}", session.id, message.payload)
        webSocketSessionMap.values.forEach { map -> map.sendMessage(message) }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info("{} closed", session.id)
        webSocketSessionMap.remove(session.id)
    }
}