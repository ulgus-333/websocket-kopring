package com.practice.socket.domain.entity.user

import com.practice.socket.domain.entity.type.Gender
import com.practice.socket.domain.entity.type.OAuth2UserType
import com.practice.socket.domain.entity.type.Role
import com.practice.socket.utils.CipherUtils
import jakarta.persistence.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Table(name = "user")
@Entity
class User private constructor (
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var idx: Long? = null,

    @Column(nullable = false, unique = true)
    val oAuth2Id: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val oAuthType: OAuth2UserType,

    @Column(nullable = false, length = 50, updatable = false)
    val email: String,

    @Column(nullable = false, length = 50, updatable = false)
    val name: String,

    @Column(unique = true, length = 30)
    var nickname: String? = null,

    var imageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val gender: Gender,

    @Column(updatable = false)
    var birth: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role
) {
    companion object {
        private const val DEFAULT_PROFILE = "user/profiles/default/default_profile.png"

        fun google(oAuth2Id: String, email: String, name: String, imageUrl: String?): User
                = User(
            oAuth2Id = oAuth2Id,
            oAuthType = OAuth2UserType.GOOGLE,
            email = email,
            name = CipherUtils.encrypt(name),
            imageUrl = imageUrl ?: DEFAULT_PROFILE,
            gender = Gender.UNKNOWN,
            role = Role.USER
        )

        fun naver(oAuth2Id: String, email: String, name: String, nickname: String?, imageUrl: String?, gender: String?, birthYear: String?, birthday: String?): User {
            val birth: LocalDate? = birthYear?.let { y -> birthday ?.let { d -> LocalDate.parse("$y-$d")}}
            return User(
                oAuth2Id = oAuth2Id,
                oAuthType = OAuth2UserType.NAVER,
                email = email,
                name = CipherUtils.encrypt(name),
                nickname = nickname,
                imageUrl = imageUrl ?: DEFAULT_PROFILE,
                gender = gender?.let(Gender::findByValue) ?: Gender.UNKNOWN,
                birth = birth,
                role = Role.USER
            )
        }

        fun kakao(oAuth2Id: String, email: String, name: String, nickname: String?, imageUrl: String?, gender: String?, birthYear: String?, birthday: String?): User {
            val birth: LocalDate? = birthYear?.let { y -> birthday?.let { d -> LocalDate.parse("$y$d", DateTimeFormatter.ofPattern("yyyyMMdd"))} }
            return User(
                oAuth2Id = oAuth2Id,
                oAuthType = OAuth2UserType.KAKAO,
                email = email,
                name = CipherUtils.encrypt(name),
                nickname = nickname,
                imageUrl = imageUrl ?: DEFAULT_PROFILE,
                gender = gender?.let{ g -> Gender.valueOf(g.uppercase()) } ?: Gender.UNKNOWN,
                birth = birth,
                role = Role.USER
            )
        }
    }

    fun update(nickname: String?, imageUrl: String?) {
        this.nickname = nickname
        imageUrl?.let { this.imageUrl = imageUrl }
            ?:let { this.nickname = DEFAULT_PROFILE }
    }

    fun role(): String {
        return role.role
    }

    fun decryptName(): String {
        return CipherUtils.decrypt(this.name)
    }
}