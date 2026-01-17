package com.practice.socket.domain.entity.chat

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Table(name = "room")
@Entity
data class Room (
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var idx: Long? = null,

    @Column(nullable = false, length = 50)
    var title: String?,

    @Column(nullable = false, columnDefinition = "datetime")
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    val createAt: LocalDateTime = LocalDateTime.now(),

    var lastMessagedAt: LocalDateTime
)
