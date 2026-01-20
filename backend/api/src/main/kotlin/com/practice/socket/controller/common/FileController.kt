package com.practice.socket.controller.common

import com.practice.socket.domain.dto.CustomOAuth2User
import com.practice.socket.domain.presentation.request.file.PARRequestDto
import com.practice.socket.domain.presentation.response.file.FileResponseDto
import com.practice.socket.service.common.FileService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/files"])
@RestController
class FileController (
    private val fileService: FileService
) {
    @PostMapping("/presigned")
    fun generatePresigendUrl(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                             @RequestBody @Valid requestDto: PARRequestDto): ResponseEntity<FileResponseDto> {

        return ResponseEntity.ok(fileService.generateParWriteUrl(requestUser, requestDto))
    }

    @DeleteMapping("/presigned/expire")
    fun expirePar(@AuthenticationPrincipal requestUser: CustomOAuth2User,
                  @ModelAttribute @Valid requestDto: PARRequestDto): ResponseEntity<Void> {
        fileService.expireRemainPAR(requestUser, requestDto)
        return ResponseEntity.ok().build()
    }
}