package com.practice.common.repository.chat

import com.practice.common.domain.entity.chat.Room
import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository: JpaRepository<Room, Long> {
}