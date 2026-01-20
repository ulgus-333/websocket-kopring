package com.practice.api.service.chat

import com.practice.common.domain.entity.chat.UserRoomRelation
import java.util.*
import java.util.stream.Collectors

data class UserRoomRelations (
    val relations: List<UserRoomRelation>
) {
    companion object {
        fun from(relations: List<UserRoomRelation>): UserRoomRelations {
            if (relations.isEmpty()) {
                return empty()
            }
            return UserRoomRelations(relations)
        }

        fun empty(): UserRoomRelations = UserRoomRelations(Collections.emptyList())
    }

    fun roomIdxes(): List<Long> {
        return this.relations.stream()
            .map(UserRoomRelation::roomIdx)
            .toList()
    }

    fun roomIdxUserNameMapper(): Map<Long, List<String>> {
        return this.relations.stream()
            .collect(Collectors.groupingBy(
                UserRoomRelation::roomIdx,
                Collectors.mapping(UserRoomRelation::username, Collectors.toList())))
    }

    fun increaseUnreadCount() {
        this.relations.forEach(UserRoomRelation::increaseUnreadMessageCount)
    }

    fun containUser(userIdx: Long): Boolean {
        return this.relations.stream()
            .anyMatch { relation -> relation.user.idx!! == userIdx }
    }
}
