package com.practice.socket.domain.dao

import com.practice.socket.domain.entity.user.QUser.user
import com.practice.socket.util.CipherUtils
import com.querydsl.core.BooleanBuilder
import org.springframework.data.domain.Pageable

data class UserSearchDao (
    val requestUserIdx: Long,
    val name: String?,
    val email: String?,
    val pageable: Pageable
) {
    fun condition(): BooleanBuilder {
        val builder = BooleanBuilder()
        builder.and(user.idx.ne(requestUserIdx))

        val conditionBuilder = BooleanBuilder()

        name?.takeIf { it.isNotBlank() }
            ?.let { conditionBuilder.and(user.name.eq(CipherUtils.encrypt(name))) }
        email?.takeIf { it.isNotBlank() }
            ?.let { conditionBuilder.and(user.email.contains(CipherUtils.decrypt(email))) }

        return builder.and(conditionBuilder)
    }

    fun offset(): Long = this.pageable.offset

    fun limit(): Long = this.pageable.pageSize.toLong()
}
