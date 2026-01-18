package com.practice.socket.service.chat

import com.practice.socket.domain.entity.chat.Message
import com.practice.socket.repository.chat.MessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class MessageService (
    private val messageRepository: MessageRepository
) {
    fun findMessagesByRoomIdx(roomIdx: Long, pageable: Pageable): Page<Message> {
        return messageRepository.findAllByRoomIdx(roomIdx, pageable)
    }

}
