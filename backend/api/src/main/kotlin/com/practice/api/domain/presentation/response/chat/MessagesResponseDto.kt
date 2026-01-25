package com.practice.api.domain.presentation.response.chat

import org.springframework.data.domain.Page
import org.springframework.data.web.PagedModel

data class MessagesResponseDto (
    val messages: PagedModel<MessageResponseDto>
) {
    companion object {
        fun from(messages: Page<MessageResponseDto>): MessagesResponseDto {
            return MessagesResponseDto(PagedModel(messages))
        }
    }
}
