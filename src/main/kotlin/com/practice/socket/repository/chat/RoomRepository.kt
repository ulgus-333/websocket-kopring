package com.practice.socket.repository.chat

import com.practice.socket.domain.entity.chat.Room
import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository: JpaRepository<Room, Long> {
}