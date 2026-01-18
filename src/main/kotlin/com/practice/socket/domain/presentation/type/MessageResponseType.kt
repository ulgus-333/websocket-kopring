package com.practice.socket.domain.presentation.type

import com.practice.socket.domain.entity.type.MessageType

enum class MessageResponseType {
    TEXT,
    IMAGE,
    SYSTEM,
    ;

    companion object {
        fun fromEntity(type: MessageType)
            = when (type) {
                MessageType.TEXT -> TEXT
                MessageType.SYSTEM -> SYSTEM
                MessageType.IMAGE -> IMAGE
            }
    }
}
