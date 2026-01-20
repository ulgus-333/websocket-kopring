package com.practice.api.repository.user

import com.practice.api.domain.dao.UserSearchDao
import com.practice.common.domain.entity.user.User
import org.springframework.data.domain.Page

interface UserCustomRepository {
    fun findUsersBySearchDao(searchDao: UserSearchDao): Page<User>
}