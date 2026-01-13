package com.practice.socket.domain.entity.type

import com.practice.socket.domain.entity.user.User
import com.practice.socket.util.UserFactory
import org.springframework.util.Assert

enum class OAuth2UserType (
    val registrationId: String,
    val userFactory: (Map<String, Any>) -> User,
    val attributeName: String
) {
    GOOGLE("google", UserFactory::googleUser, ""),
    NAVER("naver", UserFactory::naverUser, "response"),
//    KAKAO("kakao", UserFactory::kakaoUser, "profile"),
    ;

    companion object {
        private val MAPPER: Map<String, OAuth2UserType> = entries.associateBy { it.registrationId }

        fun findByRegistrationId(registrationId: String): OAuth2UserType {
            Assert.hasLength(registrationId, "registrationId cannot be empty")

            return MAPPER[registrationId]
                ?: throw IllegalArgumentException("Unknown registration id $registrationId")
        }
    }

    fun generateUser(attributes: Map<String, Any>): User = userFactory(attributes)

    fun extractAttributes(attributes: Map<String, Any>): Map<String, Any> {
        return if (attributeName.isBlank()) {
            attributes
        } else {
            attributes[attributeName] as Map<String, Any>
        }
    }
}