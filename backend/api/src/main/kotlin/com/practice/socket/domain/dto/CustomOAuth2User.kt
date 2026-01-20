package com.practice.socket.domain.dto

import com.practice.socket.domain.entity.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User private constructor(
    val user: User,
    private val attributes: Map<String, Any>
) : OAuth2User {
    companion object {
        fun of(user: User, attributes: Map<String, Any>): CustomOAuth2User = CustomOAuth2User(user, attributes)
    }

    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(GrantedAuthority { user.role() })

    override fun getName(): String =
        user.name

    fun userIdx(): Long =
        user.idx!!
}