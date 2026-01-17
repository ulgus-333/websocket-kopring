package com.practice.socket.domain.entity.chat

import com.practice.socket.domain.entity.user.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault

@Table(name = "user_room_relation")
@Entity
data class UserRoomRelation (
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var idx: Long? = null,

    @JoinColumn(name = "user_idx", nullable = false)
    @ManyToOne
    val user: User,

    @JoinColumn(name = "room_idx", nullable = false)
    @ManyToOne
    val room: Room,

    @ColumnDefault(value = "0")
    @Column(nullable = false)
    var unreadMessageCount: Int
)
