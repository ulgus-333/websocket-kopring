package com.practice.common.repository.chat

import com.practice.common.domain.entity.chat.MessageFile
import org.springframework.data.jpa.repository.JpaRepository

interface MessageFileRepository: JpaRepository<MessageFile, Long> {
}