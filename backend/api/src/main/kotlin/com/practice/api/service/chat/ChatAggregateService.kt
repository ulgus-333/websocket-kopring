package com.practice.api.service.chat

import com.practice.api.domain.dto.CustomOAuth2User
import com.practice.api.domain.presentation.request.chat.CreateChatRoomRequestDto
import com.practice.api.domain.presentation.response.chat.ChatRoomResponseDto
import com.practice.api.domain.presentation.response.chat.ChatRoomsResponseDto
import com.practice.api.domain.presentation.response.chat.MessageResponseDto
import com.practice.api.domain.presentation.response.chat.MessagesResponseDto
import com.practice.common.domain.entity.chat.Room
import com.practice.common.domain.entity.chat.UserRoomRelation
import com.practice.common.domain.entity.user.User
import com.practice.infra.service.file.FileService
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
    private val messageService: MessageService,
    private val fileService: FileService
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

    fun getRoomMessages(requestUser: CustomOAuth2User, roomIdx: Long, pageable: Pageable): MessagesResponseDto {
        val relations = relationService.findUserRoomRelationByRoomIdx(roomIdx)

        if (!relations.containUser(requestUser.userIdx())) {
            throw HttpClientErrorException(HttpStatus.BAD_REQUEST, "User is not in room")
        }

        val messages = messageService.findMessagesByRoomIdx(roomIdx, pageable)

        val responseMessage = messages.map { message ->
            if (message.isFileType()) {
                val filePath = fileService.generateParReadUrl(message.message).parUrl
                return@map MessageResponseDto.from(message, filePath)
            }
            return@map MessageResponseDto.from(message)
        }

        return MessagesResponseDto.from(responseMessage)
    }

    fun resetUnreadCount(requestUser: CustomOAuth2User, roomIdx: Long) {
        relationService.findUserRoomRelationByRoomIdxAndUserIdx(roomIdx, requestUser.userIdx())
            .resetUnreadMessageCount()
    }
}