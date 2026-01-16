package com.practice.socket.domain.presentation.response.user

import com.practice.socket.domain.entity.type.Gender
import com.practice.socket.domain.entity.user.User
import com.practice.socket.util.CipherUtils
import java.time.LocalDate

data class UserDetailResponseDto(
    val idx: Long,
    val email: String,
    val name: String,
    val nickName: String?,
    val profileImage: String?,
    val gender: Gender,
    val birth: LocalDate?
) {
    companion object {
        fun from(user: User): UserDetailResponseDto = UserDetailResponseDto(
            idx = user.idx!!,
            email = user.email,
            name = CipherUtils.decrypt(user.name),
            nickName = user.nickname,
            profileImage = user.imageUrl,
            gender = user.gender,
            birth = user.birth
        )

        fun from(user: User, imageUrl: String): UserDetailResponseDto {
            return UserDetailResponseDto(
                idx = user.idx!!,
                email = user.email,
                name = CipherUtils.decrypt(user.name),
                nickName = user.nickname,
                profileImage = imageUrl,
                gender = user.gender,
                birth = user.birth
            )
        }
    }
}
