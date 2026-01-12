package com.practice.socket.repository.user

import com.practice.socket.domain.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>

}