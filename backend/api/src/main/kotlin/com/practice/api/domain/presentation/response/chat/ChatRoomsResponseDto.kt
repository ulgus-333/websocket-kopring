package com.practice.api.domain.presentation.response.chat

import com.practice.common.domain.entity.chat.UserRoomRelation
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedModel
import java.util.*

data class ChatRoomsResponseDto (
    val rooms: PagedModel<ChatRoomResponseDto>
) {
    companion object {
        fun of(relations: Page<UserRoomRelation>, userNameMapper: Map<Long, List<String>>): ChatRoomsResponseDto {
            val pagedResponse = relations.map { relation -> ChatRoomResponseDto.of(relation, userNameMapper.getOrDefault(relation.room.idx!!, Collections.emptyList())) }
            return ChatRoomsResponseDto(PagedModel(pagedResponse))
        }

         fun empty(): ChatRoomsResponseDto {
             return ChatRoomsResponseDto(PagedModel(Page.empty()))
         }
    }
}
