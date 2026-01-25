package com.practice.socket.domain.presentation.response

import com.practice.common.domain.entity.chat.Message
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

        fun from(message: Message, filePath: String): MessageResponseDto
            = MessageResponseDto(
                idx = message.idx!!,
                type = MessageResponseType.fromEntity(message.type),
                user = UserDetailResponseDto.from(message.user),
                message = filePath,
                messagedAt = message.createAt
            )
    }
}
