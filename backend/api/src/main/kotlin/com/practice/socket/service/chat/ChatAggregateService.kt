package com.practice.socket.service.chat

import com.practice.socket.domain.dto.CustomOAuth2User
import com.practice.socket.domain.entity.chat.Room
import com.practice.socket.domain.entity.chat.UserRoomRelation
import com.practice.socket.domain.entity.user.User
import com.practice.socket.domain.presentation.request.chat.CreateChatRoomRequestDto
import com.practice.socket.domain.presentation.response.chat.ChatRoomResponseDto
import com.practice.socket.domain.presentation.response.chat.ChatRoomsResponseDto
import com.practice.socket.domain.presentation.response.chat.MessagesResponseDto
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import java.util.stream.Stream

@Transactional(readOnly = true)
@Service
class ChatAggregateService(
    private val roomService: RoomService,
    private val relationService: UserRoomRelationService,
    private val messageService: MessageService
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

    @Transactional
    fun createChatRoom(requestUser: CustomOAuth2User, targetUser: User, requestDto: CreateChatRoomRequestDto): ChatRoomResponseDto {
        val newRoom = roomService.insert(Room.insert(requestDto.title, requestDto.requestDatetime))
        val users = ArrayList<String>()
        val relations = Stream.of(requestUser.user, targetUser)
            .peek { users.add(it.decryptName()) }
            .map { UserRoomRelation.insert(it, newRoom) }
            .toList()

        val myRelation = relationService.insertAll(relations).stream()
            .filter{ relation -> relation.user.idx == requestUser.userIdx() }
            .findFirst()
            .orElseThrow()

        return ChatRoomResponseDto.of(myRelation, users)
    }

    fun getRoomMessages(requestUser: CustomOAuth2User, roomIdx: Long, pageable: Pageable): MessagesResponseDto? {
        val relations = relationService.findUserRoomRelationByRoomIdx(roomIdx)

        if (!relations.containUser(requestUser.userIdx())) {
            throw HttpClientErrorException(HttpStatus.BAD_REQUEST, "User is not in room")
        }

        val messages = messageService.findMessagesByRoomIdx(roomIdx, pageable)
        return MessagesResponseDto.from(messages)
    }

    fun resetUnreadCount(requestUser: CustomOAuth2User, roomIdx: Long) {
        relationService.findUserRoomRelationByRoomIdxAndUserIdx(roomIdx, requestUser.userIdx())
            .resetUnreadMessageCount()
    }
}