package com.practice.socket.config.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CipherProperties {
    @Value("\${cipher.seed}")
    fun setSeed(value: String) {
        seed = value
    }

    companion object {
        private lateinit var seed: String

        fun seed(): String = seed
    }
}