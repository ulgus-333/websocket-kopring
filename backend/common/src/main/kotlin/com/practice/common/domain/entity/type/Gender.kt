package com.practice.common.domain.entity.type

import org.springframework.util.Assert

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