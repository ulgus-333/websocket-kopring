package com.practice.socket.repository.user

import com.practice.socket.domain.dao.UserSearchDao
import com.practice.socket.domain.entity.user.QUser.user
import com.practice.socket.domain.entity.user.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class UserCustomRepositoryImpl(
    var queryFactory: JPAQueryFactory
): UserCustomRepository {

    override fun findUsersBySearchDao(searchDao: UserSearchDao): Page<User> {
        val searchCount = queryFactory.select(user.count())
            .from(user)
            .where(searchDao.condition())
            .fetchOne() ?: return Page.empty()

        if (searchCount == 0L) {
            return Page.empty()
        }

        val users = queryFactory.selectFrom(user)
            .where(searchDao.condition())
            .offset(searchDao.offset())
            .limit(searchDao.limit())
            .orderBy(user.name.asc())
            .fetch()

        return PageImpl(users, searchDao.pageable, searchCount)
    }
}