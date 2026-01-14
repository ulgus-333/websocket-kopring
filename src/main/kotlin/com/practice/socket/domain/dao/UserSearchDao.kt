package com.practice.socket.domain.dao

import com.querydsl.core.BooleanBuilder
import org.springframework.data.domain.Pageable

data class UserSearchDao (
    val requestUserIdx: Long,
    val name: String?,
    val email: String?,
    val pageable: Pageable
) {
    fun condition(): BooleanBuilder {
        val builder = BooleanBuilder();
        return builder
    }
}
