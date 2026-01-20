package com.practice.socket.service.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.entity.type.OAuth2UserType
import com.practice.socket.repository.user.UserRepository
import com.practice.socket.utils.UserFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
): DefaultOAuth2UserService() {
    private var log: Logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)

    override fun loadUser(userRequest: OAuth2UserRequest?): CustomOAuth2User? {
        val oAuth2User = super.loadUser(userRequest)
        log.info("Attributes: {}", oAuth2User.attributes.toString())
        val oAuth2UserType = userRequest?.clientRegistration?.let { OAuth2UserType.findByRegistrationId(it.registrationId) }

        val attribute = oAuth2User.attributes
        val oAuth2Id = oAuth2UserType?.let { UserFactory.idExtractor(attribute, it) }

        val user = oAuth2Id?.let { it ->
            userRepository.findByOAuth2Id(it)
                .orElseGet { oAuth2UserType.let { userRepository.save(it.generateUser(attribute)) } }
        }

        return user?.let { CustomOAuth2User.of(it, attribute) }
    }
}