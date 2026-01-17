package com.practice.socket.controller.chat

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.response.chat.ChatRoomsResponseDto
import com.practice.socket.service.chat.ChatAggregateService
import com.practice.socket.service.user.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
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
}