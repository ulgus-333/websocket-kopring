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

    @Column(nullable = false)
    val id: String,

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

        fun google(id: String, email: String, name: String, imageUrl: String?): User
            = User(
                id = id,
                email = email,
                name = CipherUtils.encrypt(name),
                imageUrl = imageUrl ?: DEFAULT_PROFILE,
                role = Role.USER
            )

        fun naver(id: String, email: String, name: String, nickname: String?, imageUrl: String?, gender: String?, birthYear: String?, birthday: String?): User {
            val birth: LocalDate? = birthYear?.let { y -> birthday ?.let { d -> LocalDate.parse("$y-$d")}}
            return User(
                id = id,
                email = email,
                name = CipherUtils.encrypt(name),
                nickname = nickname,
                imageUrl = imageUrl ?: DEFAULT_PROFILE,
                gender = gender?.let(Gender::findByValue),
                birth = birth,
                role = Role.USER
            )
        }
        //kakao
//Attributes: {id=4693631336, connected_at=2026-01-12T14:28:57Z, properties={nickname=지현, profile_image=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_640x640.jpg, thumbnail_image=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_110x110.jpg}, kakao_account={profile_nickname_needs_agreement=false, profile_image_needs_agreement=false, profile={nickname=지현, thumbnail_image_url=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_110x110.jpg, profile_image_url=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_640x640.jpg, is_default_image=false, is_default_nickname=false}, name_needs_agreement=false, name=김지현, has_email=true, email_needs_agreement=false, is_email_valid=true, is_email_verified=true, email=kenj_h@naver.com, has_birthyear=true, birthyear_needs_agreement=false, birthyear=1992, has_birthday=true, birthday_needs_agreement=false, birthday=0316, birthday_type=SOLAR, is_leap_month=false, has_gender=true, gender_needs_agreement=false, gender=male}}
    }

    fun role(): String {
        return role.role
    }
}