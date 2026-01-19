package com.practice.socket.domain.presentation.response.chat

import com.practice.socket.domain.entity.chat.Message
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedModel

data class MessagesResponseDto (
    val messages: PagedModel<MessageResponseDto>
) {
    companion object {
        fun from(messages: Page<Message>): MessagesResponseDto {
            val response = messages.map(MessageResponseDto::from)
            return MessagesResponseDto(PagedModel(response))
        }
    }
}
