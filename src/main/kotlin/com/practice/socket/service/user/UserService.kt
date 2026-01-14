package com.practice.socket.service.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.request.user.UserSearchRequestDto
import com.practice.socket.domain.presentation.response.user.UserDetailResponseDto
import com.practice.socket.domain.presentation.response.user.UserDetailsResponseDto
import com.practice.socket.repository.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

@Service
class UserService (
    private val userRepository: UserRepository,
) {
    fun findUserDetail(requestUser: CustomOAuth2User): UserDetailResponseDto? {
        val user = userRepository.findById(requestUser.userIdx())
            .orElseThrow{ HttpClientErrorException(HttpStatusCode.valueOf(404), "NotFound User") }
        return UserDetailResponseDto.from(user)
    }

    fun searchUsers(requestUser: CustomOAuth2User, requestDto: UserSearchRequestDto, pageable: Pageable): UserDetailsResponseDto {
        TODO("Not yet implemented")
    }
}