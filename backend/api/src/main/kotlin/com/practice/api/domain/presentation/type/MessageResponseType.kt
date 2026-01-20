package com.practice.api.domain.presentation.type

import com.practice.common.domain.entity.type.MessageType

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
