package com.practice.api.controller.user

import com.practice.api.domain.dto.CustomOAuth2User
import com.practice.api.domain.presentation.request.user.UserProfileUpdateRequestDto
import com.practice.api.domain.presentation.request.user.UserSearchRequestDto
import com.practice.api.domain.presentation.response.user.UserDetailResponseDto
import com.practice.api.domain.presentation.response.user.UserDetailsResponseDto
import com.practice.api.service.user.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

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
                    pageable: Pageable
    ):  ResponseEntity<UserDetailsResponseDto> {

        return ResponseEntity.ok(userService.searchUsers(requestUser, requestDto, pageable));
    }

    @PatchMapping("/profile")
    fun updateProfile(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                      @RequestBody @Valid requestDto: UserProfileUpdateRequestDto
    ): ResponseEntity<Void> {

        userService.updateUserProfile(requestUser, requestDto)
        return ResponseEntity.ok().build()
    }
}