package com.practice.socket.domain.entity.type

import com.mysema.commons.lang.Assert
import java.lang.IllegalArgumentException

enum class Gender (
    val value: String
) {
    MALE("M"),
    FEMALE("F"),
    UNKNOWN("");

    companion object {
        private val MAPPER: Map<String, Gender> = entries.associateBy { it.value }

        fun findByValue(value: String): Gender {
            Assert.hasLength(value, "value cannot be empty")

            return MAPPER[value]
                ?: UNKNOWN
        }
    }
}