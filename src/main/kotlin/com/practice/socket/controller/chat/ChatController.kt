package com.practice.socket.controller.chat

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.request.chat.CreateChatRoomRequestDto
import com.practice.socket.domain.presentation.response.chat.ChatRoomResponseDto
import com.practice.socket.domain.presentation.response.chat.ChatRoomsResponseDto
import com.practice.socket.service.chat.ChatAggregateService
import com.practice.socket.service.user.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}