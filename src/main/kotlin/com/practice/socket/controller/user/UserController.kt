package com.practice.socket.controller.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.request.user.UserSearchRequestDto
import com.practice.socket.domain.presentation.response.user.UserDetailResponseDto
import com.practice.socket.domain.presentation.response.user.UserDetailsResponseDto
import com.practice.socket.service.user.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/user"])
@RestController
class UserController (
    private val userService: UserService
) {

    @GetMapping
    fun getUserDetail(@AuthenticationPrincipal requestUser: CustomOAuth2User): ResponseEntity<UserDetailResponseDto> {

        return ResponseEntity.ok(userService.findUserDetail(requestUser))
    }

    @GetMapping("/search")
    fun searchUsers(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                    @ModelAttribute requestDto: UserSearchRequestDto,
                    pageable: Pageable):  ResponseEntity<UserDetailsResponseDto> {

        return ResponseEntity.ok(userService.searchUsers(requestUser, requestDto, pageable));
    }
}