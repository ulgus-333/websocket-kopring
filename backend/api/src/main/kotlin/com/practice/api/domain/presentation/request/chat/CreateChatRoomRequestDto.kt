package com.practice.api.domain.presentation.request.chat

import java.time.LocalDateTime

data class CreateChatRoomRequestDto (
    val title: String?,
    val invitedUserIdx: Long
) {
    val requestDatetime: LocalDateTime = LocalDateTime.now()
}