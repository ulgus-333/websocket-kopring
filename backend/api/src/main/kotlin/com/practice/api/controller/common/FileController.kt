package com.practice.api.controller.common

import com.practice.api.domain.dto.CustomOAuth2User
import com.practice.api.domain.presentation.request.file.PARRequestDto
import com.practice.api.domain.presentation.response.file.FileResponseDto
import com.practice.infra.service.file.FileService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping(value = ["/files"])
@RestController
class FileController (
    private val fileService: FileService
) {
    @PostMapping("/presigned")
    fun generatePresigendUrl(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                             @RequestBody @Valid requestDto: PARRequestDto
    ): ResponseEntity<FileResponseDto> {

        val parPathDto = fileService.generateParWriteUrl(
            requestDto.pathType.convert(),
            requestDto.filename,
            requestUser.userIdx().toString()
        )

        return ResponseEntity.ok(FileResponseDto.from(parPathDto))
    }

    @DeleteMapping("/presigned/expire")
    fun expirePar(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                  @ModelAttribute @Valid requestDto: PARRequestDto
    ): ResponseEntity<Void> {

        fileService.expireRemainPar(
            requestDto.pathType.convert(),
            requestDto.filename,
            requestUser.userIdx().toString()
        )

        return ResponseEntity.ok().build()
    }
}