package com.practice.common.domain.entity.chat

import com.practice.common.domain.entity.type.MessageType
import com.practice.common.domain.entity.user.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Table(name = "message")
@Entity
class Message private constructor (
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var idx: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: MessageType = MessageType.TEXT,

    @Column(columnDefinition = "text", nullable = false)
    val message: String,

    @JoinColumn(name = "user_idx", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @JoinColumn(name = "room_idx", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val room: Room,

    @Column(nullable = false, columnDefinition = "datetime")
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    val createAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, columnDefinition = "boolean")
    @ColumnDefault(value = "false")
    val isDelete: Boolean,
    ) {
}