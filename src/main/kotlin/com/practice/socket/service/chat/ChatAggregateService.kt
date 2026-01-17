package com.practice.socket.service.chat

import com.practice.socket.domain.presentation.response.chat.ChatRoomsResponseDto
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ChatAggregateService (
    private val relationService: UserRoomRelationService
) {
    fun findRoomsByUserIdx(userIdx: Long, pageable: Pageable): ChatRoomsResponseDto {
        val pagedRelation = relationService.findPagedUserRoomRelationByUserIdx(userIdx, pageable)

        if (pagedRelation.isEmpty) {
            return ChatRoomsResponseDto.empty()
        }

        val roomRelations = relationService.findUserRoomRelationByRoomIdxes(pagedRelation.content)
        val mapper = roomRelations.roomIdxUserNameMapper()

        return ChatRoomsResponseDto.of(pagedRelation, mapper)
    }
}