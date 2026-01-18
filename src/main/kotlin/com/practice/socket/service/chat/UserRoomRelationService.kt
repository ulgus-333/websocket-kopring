package com.practice.socket.service.chat

import com.practice.socket.domain.entity.chat.UserRoomRelation
import com.practice.socket.repository.chat.UserRoomRelationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException

@Transactional(readOnly = true)
@Service
class UserRoomRelationService (
    private val userRoomRelationRepository: UserRoomRelationRepository
) {
    fun findPagedUserRoomRelationByUserIdx(userIdx: Long, pageable: Pageable): Page<UserRoomRelation> {
        return userRoomRelationRepository.findAllByUserIdx(userIdx, pageable)
    }

    fun findUserRoomRelationByRoomIdxes(targetRelation: List<UserRoomRelation>): UserRoomRelations {
        val relations = UserRoomRelations.from(targetRelation)
        return UserRoomRelations.from(userRoomRelationRepository.findAllByRoomIdxIn(relations.roomIdxes()))
    }

    @Transactional
    fun insertAll(relations: List<UserRoomRelation>): List<UserRoomRelation> {
        return userRoomRelationRepository.saveAll(relations)
    }

    fun findUserRoomRelationByRoomIdx(roomIdx: Long): UserRoomRelations {
        return UserRoomRelations.from(userRoomRelationRepository.findAllByRoomIdx(roomIdx))
    }

    fun findUserRoomRelationByRoomIdxAndUserIdx(roomIdx: Long, userIdx: Long): UserRoomRelation {
        return userRoomRelationRepository.findByRoomIdxAndUserIdx(roomIdx, userIdx)
            ?: throw HttpClientErrorException(HttpStatus.NOT_FOUND)
    }
}
