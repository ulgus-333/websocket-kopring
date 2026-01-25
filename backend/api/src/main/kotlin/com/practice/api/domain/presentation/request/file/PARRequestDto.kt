package com.practice.api.domain.presentation.request.file

import com.practice.api.domain.presentation.type.FilePathRequestType
import jakarta.validation.constraints.NotNull

data class PARRequestDto(
    @NotNull
    val pathType: FilePathRequestType,
    val filename: String,
    val variables: List<String>
)