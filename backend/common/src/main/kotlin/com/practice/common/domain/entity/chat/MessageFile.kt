package com.practice.common.domain.entity.chat

import com.practice.common.domain.entity.type.MessageFileType
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Table(name = "message_file")
@Entity
class MessageFile private constructor(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var idx: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: MessageFileType,

    @Column(length = 500, nullable = false)
    val path: String,

    @JoinColumn(name = "message_idx", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    val message: Message,

    @Column(nullable = false, columnDefinition = "datetime")
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    val createAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun new(message: Message): MessageFile
            = MessageFile(
                type = MessageFileType.convert(message.type),
                path = message.message,
                message = message,
                createAt = message.createAt
            )
    }

}
