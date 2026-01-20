package com.practice.api.domain.presentation.request.file

import com.practice.api.domain.presentation.type.FilePathType
import jakarta.validation.constraints.NotNull

data class PARRequestDto(
    @NotNull
    val pathType: FilePathType,
    val filename: String
) {
    fun generateFilePath(vararg variables: String): String {
        return pathType.generateFilePath(*variables) + filename
    }
}
