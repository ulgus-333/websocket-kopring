package com.practice.api.domain.presentation.response.file

import com.practice.infra.domain.dto.ParPathDto

data class FileResponseDto (
    val parUrl: String,
    val filePath: String,
    val fileName: String
) {
    companion object {
        fun from(parPathDto: ParPathDto): FileResponseDto {
            return FileResponseDto(
                parUrl = parPathDto.parUrl,
                filePath = parPathDto.filePath,
                fileName = parPathDto.fileName
            )
        }
    }
}
