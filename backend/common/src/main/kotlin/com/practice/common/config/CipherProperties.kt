package com.practice.common.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CipherProperties(
    @Value("\${cipher.seed}")
    private val seedValue: String
) {

    @PostConstruct
    fun init() {
        seed = seedValue
    }

    companion object {
        private lateinit var seed: String

        fun seed(): String = seed
    }
}