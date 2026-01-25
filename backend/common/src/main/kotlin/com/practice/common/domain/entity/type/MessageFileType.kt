package com.practice.common.domain.entity.type

enum class MessageFileType {
    IMAGE,
    FILE,
    ;

    companion object {
        fun convert(messageType: MessageType): MessageFileType = when (messageType) {
            MessageType.IMAGE -> MessageFileType.IMAGE
            MessageType.FILE -> MessageFileType.FILE

            else -> throw IllegalArgumentException("Unknown message file type: $messageType")
        }
    }
}