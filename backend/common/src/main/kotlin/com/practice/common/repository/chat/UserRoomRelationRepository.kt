package com.practice.common.repository.chat

import com.practice.common.domain.entity.chat.UserRoomRelation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoomRelationRepository: JpaRepository<UserRoomRelation, Long> {
    fun findAllByUserIdx(userIdx: Long, pageable: Pageable): Page<UserRoomRelation>
    fun findAllByRoomIdxIn(roomIdxes: List<Long>): List<UserRoomRelation>
    fun findAllByRoomIdx(roomIdx: Long): List<UserRoomRelation>
    fun findByRoomIdxAndUserIdx(roomIdx: Long, userIdx: Long): UserRoomRelation?
}
