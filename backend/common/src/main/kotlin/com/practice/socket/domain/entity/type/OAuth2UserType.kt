package com.practice.socket.domain.entity.type

import com.practice.socket.domain.entity.user.User
import com.practice.socket.utils.UserFactory
import org.springframework.util.Assert

enum class OAuth2UserType (
    private val registrationId: String,
    private val userFactory: (Map<String, Any>) -> User,
) {
    GOOGLE("google", UserFactory::googleUser),
    NAVER("naver", UserFactory::naverUser),
    KAKAO("kakao", UserFactory::kakaoUser),
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
}