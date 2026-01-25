package com.practice.socket.config.support

import com.practice.socket.config.WebSocketConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriTemplate
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketHandler: TextWebSocketHandler() {
    private val logger: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    companion object {
        private val rooms: ConcurrentHashMap<String, HashSet<WebSocketSession>> = ConcurrentHashMap()
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Connected session: {}", session)

        session.uri?.let { sessionUri ->
            val roomId = extractRoomId(sessionUri.path)

            roomId?.let {
                rooms.computeIfAbsent(roomId) { HashSet() }.add(session)
                return
            }
        }

        session.close(CloseStatus.BAD_DATA)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("Disconnected session: {}", session)

        session.uri?.let { sessionUri ->
            val roomId = extractRoomId(sessionUri.path)
            val targetSession: HashSet<WebSocketSession>? = rooms[roomId]
            targetSession?.let {
                it.remove(session)
                if (it.isEmpty()) {
                    rooms.remove(roomId)
                }
            }
        }
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info("{} send message: {}", session.id, message.payload)

        session.uri?.let { sessionUri ->
            val roomId = extractRoomId(sessionUri.path)
            rooms[roomId]?.let {
                it.forEach { s -> s.sendMessage(message) }
            }
        }
    }

    private fun extractRoomId(sessionPath: String): String? {
        val pathVariables = UriTemplate(WebSocketConfig.SESSION_URI).match(sessionPath)
        return pathVariables["roomId"]
    }
}