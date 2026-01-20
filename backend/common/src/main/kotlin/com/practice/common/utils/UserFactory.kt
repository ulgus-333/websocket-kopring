package com.practice.common.utils

import com.practice.common.domain.entity.type.OAuth2UserType
import com.practice.common.domain.entity.user.User

object UserFactory {
    fun googleUser(attribute: Map<String, Any>): User {
        return User.google(
            attribute["sub"] as String,
            attribute["email"] as String,
            attribute["name"] as String,
            attribute["picture"] as String,
        )
    }

    fun naverUser(attribute: Map<String, Any>): User {
        val response = attribute["response"] as Map<*, *>
        return User.naver(
            response["id"] as String,
            response["email"] as String,
            response["name"] as String,
            response["nickname"] as String,
            response["profile_image"] as String,
            response["gender"] as String,
            response["birthyear"] as String,
            response["birthday"] as String
        )
    }

    fun kakaoUser(attribute: Map<String, Any>): User {
        val account = attribute["kakao_account"] as Map<*, *>
        val profile = account["profile"] as Map<*, *>
        return User.kakao(
            (attribute["id"] as Long).toString(),
            account["email"] as String,
            account["name"] as String,
            profile["nickname"] as String,
            profile["profile_image_url"] as String,
            account["gender"] as String,
            account["birthyear"] as String,
            account["birthday"] as String,
        )
    }

    fun idExtractor(attribute: Map<String, Any>, userType: OAuth2UserType): String {
        return when (userType) {
            OAuth2UserType.GOOGLE -> attribute["sub"] as String
            OAuth2UserType.NAVER -> {
                val response = attribute["response"] as Map<*, *>
                return response["id"] as String
            }
            OAuth2UserType.KAKAO -> (attribute["id"] as Long).toString()
        }
    }
}