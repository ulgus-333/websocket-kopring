package com.practice.api.domain.presentation.response.chat

import com.practice.common.domain.entity.chat.UserRoomRelation
import java.time.LocalDateTime

data class ChatRoomResponseDto (
    val idx: Long,
    val title: String?,
    val users: List<String>,
    val unreadMessageCount: Int,
    val lastMessagedAt: LocalDateTime?
) {
    companion object {
        fun of(relation: UserRoomRelation, users: List<String>): ChatRoomResponseDto
            = ChatRoomResponseDto(
                idx = relation.room.idx!!,
                title = relation.room.title,
                users = users,
                relation.unreadMessageCount,
                relation.room.lastMessagedAt
            )
    }

}
