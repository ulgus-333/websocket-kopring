package com.practice.socket.domain.presentation.response

import com.practice.common.domain.entity.type.MessageType

enum class MessageResponseType {
    TEXT,
    IMAGE,
    FILE,
    SYSTEM,
    ;

    companion object {
        fun fromEntity(type: MessageType)
            = when (type) {
                MessageType.TEXT -> TEXT
                MessageType.SYSTEM -> SYSTEM
                MessageType.IMAGE -> IMAGE
                MessageType.FILE -> FILE
            }
    }
}
