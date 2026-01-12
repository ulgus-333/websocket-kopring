package com.practice.socket.domain.entity.user

import com.practice.socket.domain.entity.type.Gender
import com.practice.socket.domain.entity.type.Role
import com.practice.socket.util.CipherUtils
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDate

@Entity
@Table(name = "user")
class User private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idx: Long? = null,

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    val email: String,

    @Column(nullable = false, length = 50, updatable = false)
    val name: String,

    @Column(unique = true, length = 30)
    var nickname: String? = null,

    var imageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    val gender: Gender? = null,

    @Column(updatable = false)
    var birth: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role
) : Serializable {
    companion object {
        private const val DEFAULT_PROFILE = "user/profiles/default/default_profile.png"

        fun google(email: String, name: String, imageUrl: String?): User
            = User(
                email = email,
                name = CipherUtils.encrypt(name),
                imageUrl = imageUrl ?: DEFAULT_PROFILE,
                role = Role.USER
            )
    }

    fun role(): String {
        return role.role
    }
}