package com.practice.socket.controller.chat

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.request.chat.CreateChatRoomRequestDto
import com.practice.socket.domain.presentation.response.chat.ChatRoomResponseDto
import com.practice.socket.domain.presentation.response.chat.ChatRoomsResponseDto
import com.practice.socket.domain.presentation.response.chat.MessagesResponseDto
import com.practice.socket.service.chat.ChatAggregateService
import com.practice.socket.service.user.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RequestMapping("/chats")
@RestController
class ChatController (
    private val chatService: ChatAggregateService,
    private val userService: UserService
) {
    @GetMapping("/rooms")
    fun findChatRooms(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                      pageable: Pageable): ResponseEntity<ChatRoomsResponseDto> {

        return ResponseEntity.ok(chatService.findRoomsByUserIdx(requestUser.userIdx(), pageable))
    }

    @PostMapping("/room")
    fun createChatRoom(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                       @RequestBody @Valid requestDto: CreateChatRoomRequestDto): ResponseEntity<ChatRoomResponseDto> {
        val targetUser = userService.findUserByIdx(requestDto.invitedUserIdx)

        return ResponseEntity.ok(chatService.createChatRoom(requestUser, targetUser, requestDto))
    }

    @GetMapping("/{roomIdx}")
    fun getRoomMessages(
        @AuthenticationPrincipal requestUser: CustomOAuth2User,
        @PathVariable roomIdx: Long,
        @PageableDefault(size = 30, sort = ["createAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<MessagesResponseDto> {

        return ResponseEntity.ok(chatService.getRoomMessages(requestUser, roomIdx, pageable))
    }
}