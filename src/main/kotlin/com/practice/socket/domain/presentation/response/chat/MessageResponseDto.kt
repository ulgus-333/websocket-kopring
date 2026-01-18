package com.practice.socket.domain.presentation.response.chat

import com.practice.socket.domain.entity.chat.Message
import com.practice.socket.domain.presentation.response.user.UserDetailResponseDto
import com.practice.socket.domain.presentation.type.MessageResponseType
import java.time.LocalDateTime

data class MessageResponseDto (
    val idx: Long,
    val type: MessageResponseType,
    val user: UserDetailResponseDto,
    val message: String,
    val messagedAt: LocalDateTime
) {
    companion object {
        fun from(message: Message): MessageResponseDto
            = MessageResponseDto(
                idx = message.idx!!,
                type = MessageResponseType.fromEntity(message.type),
                user = UserDetailResponseDto.from(message.user),
                message = message.message,
                messagedAt = message.createAt
            )
    }
}
