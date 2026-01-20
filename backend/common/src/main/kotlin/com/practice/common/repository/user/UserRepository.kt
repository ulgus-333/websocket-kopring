package com.practice.common.repository.user

import com.practice.common.domain.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<User, Long> {
    fun findByOAuth2Id(oAuth2Id: String): Optional<User>

}