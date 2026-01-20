package com.practice.api.domain.presentation.request.user

import com.practice.api.domain.dao.UserSearchDao
import org.springframework.data.domain.Pageable

data class UserSearchRequestDto (
    val name: String?,
    val email: String?,
) {
    fun toDao(requestUserIdx: Long, pageable: Pageable): UserSearchDao {
        return UserSearchDao(
            requestUserIdx = requestUserIdx,
            name = name,
            email = email,
            pageable = pageable
        )
    }
}