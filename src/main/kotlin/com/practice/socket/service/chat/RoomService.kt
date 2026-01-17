package com.practice.socket.service.chat

import com.practice.socket.domain.entity.chat.Room
import com.practice.socket.repository.chat.RoomRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoomService (
    private val roomRepository: RoomRepository
) {
    @Transactional
    fun insert(room: Room): Room {
        return roomRepository.save(room)
    }

}
