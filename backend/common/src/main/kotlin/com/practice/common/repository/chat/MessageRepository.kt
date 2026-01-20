package com.practice.common.repository.chat

import com.practice.common.domain.entity.chat.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository: JpaRepository<Message, Long> {
    fun findAllByRoomIdx(roomIdx: Long, pageable: Pageable): Page<Message>

}
