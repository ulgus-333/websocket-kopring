package com.practice.api.domain.presentation.response.user

import com.practice.common.domain.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedModel

data class UserDetailsResponseDto (
    val users: PagedModel<UserDetailResponseDto>
) {
    companion object {
        fun from(users: Page<User>): UserDetailsResponseDto {
            val response = users.map (UserDetailResponseDto::from)

            return UserDetailsResponseDto(
                users = PagedModel(response)
            )
        }
    }
}
