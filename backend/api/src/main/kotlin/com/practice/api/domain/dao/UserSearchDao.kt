package com.practice.api.domain.dao

import com.practice.common.domain.entity.user.QUser.user
import com.practice.common.utils.CipherUtils
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
