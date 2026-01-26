package com.practice.socket.config.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.practice.common.domain.dto.MessagePayload
import com.practice.common.domain.entity.chat.MessageFile
import com.practice.common.repository.chat.MessageFileRepository
import com.practice.common.repository.chat.MessageRepository
import com.practice.common.repository.chat.RoomRepository
import com.practice.common.repository.user.UserRepository
import com.practice.infra.domain.dto.CacheKey
import com.practice.infra.domain.type.FilePathType
import com.practice.infra.service.cache.RedisService
import com.practice.infra.service.file.FileService
import com.practice.socket.config.WebSocketConfig
import com.practice.socket.domain.presentation.response.MessageResponseDto
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriTemplate
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val messageRepository: MessageRepository,
    private val messageFileRepository: MessageFileRepository,
    private val fileService: FileService,
    private val redisService: RedisService,
): TextWebSocketHandler() {
    @PostConstruct
    fun init() {
        objectMapper.registerModule(JavaTimeModule())
    }

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

        val payload = objectMapper.readValue(message.payload as String, MessagePayload::class.java)

        val user = userRepository.findById(payload.userIdx).orElseThrow()
        val room = roomRepository.findById(payload.roomIdx).orElseThrow()
        val newMessage = payload.convertToEntity(user, room)
        messageRepository.save(newMessage)

        var response: MessageResponseDto? = null;

        if (newMessage.isFileType()) {
            val messageFile = messageFileRepository.save(MessageFile.new(newMessage))
            val parPathDto = fileService.generateParReadUrl(newMessage.message)
            response = MessageResponseDto.from(newMessage, parPathDto.parUrl)
            val cacheKey = CacheKey.OCI_USER_READ_KEY.generateKey(messageFile.idx.toString(), FilePathType.CHAT_ATTACHMENT.name)
            redisService.set(cacheKey, parPathDto, CacheKey.OCI_USER_READ_KEY.expire())
        }

        response = response?:(MessageResponseDto.from(newMessage))

        session.uri?.let { sessionUri ->
            val roomId = extractRoomId(sessionUri.path)
            rooms[roomId]?.let {
                it.forEach { s -> s.sendMessage(TextMessage(objectMapper.writeValueAsString(response))) }
            }
        }
    }

    private fun extractRoomId(sessionPath: String): String? {
        val pathVariables = UriTemplate(WebSocketConfig.SESSION_URI).match(sessionPath)
        return pathVariables["roomId"]
    }
}