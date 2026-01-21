package com.practice.api.domain.presentation.type

import com.practice.infra.domain.type.FilePathType

enum class FilePathRequestType {
    PROFILE,
    ;

    fun convert(): FilePathType {
        return when(this) {
            PROFILE -> FilePathType.PROFILE
        }
    }
}