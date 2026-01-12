package com.practice.socket.util

import com.practice.socket.domain.entity.user.User

object UserFactory {
    fun googleUser(attribute: Map<String, Any>): User {
        return User.google(
            attribute.get("email") as String,
            attribute.get("name") as String,
            attribute.get("picture") as String,
        )
    }
}