package com.practice.socket.util

import com.practice.socket.domain.entity.user.User

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
        return User.naver(
            attribute["id"] as String,
            attribute["email"] as String,
            attribute["name"] as String,
            attribute["nickname"] as String,
            attribute["profile_image"] as String,
            attribute["gender"] as String,
            attribute["birthyear"] as String,
            attribute["birthday"] as String
        )
    }

//    fun kakaoUser(attribute: Map<String, Any>): User {
//        return User.kakao(
//
//        )
//    }
}