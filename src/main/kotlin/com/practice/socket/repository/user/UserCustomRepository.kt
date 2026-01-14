package com.practice.socket.repository.user

import com.practice.socket.domain.dao.UserSearchDao
import com.practice.socket.domain.entity.user.User
import org.springframework.data.domain.Page

interface UserCustomRepository {
    fun findUsersBySearchDao(searchDao: UserSearchDao): Page<User>
}