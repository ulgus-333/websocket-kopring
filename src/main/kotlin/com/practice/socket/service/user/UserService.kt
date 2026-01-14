package com.practice.socket.service.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.presentation.response.UserDetailResponseDto
import com.practice.socket.repository.user.UserRepository
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

@Service
class UserService (
    private val userRepository: UserRepository,
) {
    fun findUserDetail(requestUser: CustomOAuth2User): Any? {
        val user = userRepository.findById(requestUser.userIdx())
            .orElseThrow{ HttpClientErrorException(HttpStatusCode.valueOf(404), "NotFound User") }
        return UserDetailResponseDto.from(user)
    }
}