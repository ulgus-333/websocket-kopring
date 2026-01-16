package com.practice.socket.service.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.request.user.UserProfileUpdateRequestDto
import com.practice.socket.domain.presentation.request.user.UserSearchRequestDto
import com.practice.socket.domain.presentation.response.user.UserDetailResponseDto
import com.practice.socket.domain.presentation.response.user.UserDetailsResponseDto
import com.practice.socket.repository.user.UserCustomRepository
import com.practice.socket.repository.user.UserRepository
import com.practice.socket.service.common.FileService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException

@Service
class UserService (
    private val userRepository: UserRepository,
    private val userCustomRepository: UserCustomRepository,
    private val fileService: FileService
) {
    @Transactional(readOnly = true)
    fun findUserDetail(requestUser: CustomOAuth2User): UserDetailResponseDto? {
        val user = userRepository.findById(requestUser.userIdx())
            .orElseThrow{ HttpClientErrorException(HttpStatusCode.valueOf(404), "NotFound User") }
        return UserDetailResponseDto.from(user)
    }

    @Transactional(readOnly = true)
    fun searchUsers(requestUser: CustomOAuth2User, requestDto: UserSearchRequestDto, pageable: Pageable): UserDetailsResponseDto {
        val searchDao = requestDto.toDao(requestUser.userIdx(), pageable)
        val users = userCustomRepository.findUsersBySearchDao(searchDao)

        return UserDetailsResponseDto.from(users)
    }

    @Transactional
    fun updateUserProfile(requestUser: CustomOAuth2User, requestDto: UserProfileUpdateRequestDto) {
        val user = userRepository.findById(requestUser.userIdx())
            .orElseThrow { HttpClientErrorException(HttpStatus.valueOf(404), "NotFound User") }
        user.update(requestDto.nickname, requestDto.profileImageUrl)
    }
}