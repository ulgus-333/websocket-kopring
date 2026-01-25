package com.practice.common.domain.dto

import com.practice.common.domain.entity.chat.Message
import com.practice.common.domain.entity.chat.Room
import com.practice.common.domain.entity.type.MessageType
import com.practice.common.domain.entity.user.User
import java.time.LocalDateTime

data class MessagePayload (
    val type: MessageType,
    val message: String,
    val userIdx: Long,
    val roomIdx: Long,
    val createAt: LocalDateTime,
    val senderNickname: String
) {
    fun convertToEntity(user: User, room: Room): Message
        = Message.new(type, message, user, room, createAt)
}