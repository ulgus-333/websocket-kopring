package com.practice.socket.service.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.entity.type.OAuth2UserType
import com.practice.socket.repository.user.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
): DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val oAuth2UserType = userRequest?.clientRegistration?.let { OAuth2UserType.findByRegistrationId(it.registrationId) }

        val attribute = oAuth2User.attributes
        val email = attribute["email"] as String

        val user = userRepository.findByEmail(email)
            .orElseGet { oAuth2UserType?.let { userRepository.save(it.generateUser(attribute)) } }

        return CustomOAuth2User.of(user, attribute)
    }
}