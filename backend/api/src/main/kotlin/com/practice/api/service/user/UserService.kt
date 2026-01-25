package com.practice.api.service.user

import com.practice.api.domain.dto.CustomOAuth2User
import com.practice.api.domain.presentation.request.user.UserProfileUpdateRequestDto
import com.practice.api.domain.presentation.request.user.UserSearchRequestDto
import com.practice.api.domain.presentation.response.user.UserDetailResponseDto
import com.practice.api.domain.presentation.response.user.UserDetailsResponseDto
import com.practice.api.domain.presentation.type.FilePathRequestType
import com.practice.api.repository.user.UserCustomRepository
import com.practice.infra.domain.dto.CacheKey
import com.practice.common.domain.entity.user.User
import com.practice.common.repository.user.UserRepository
import com.practice.infra.domain.dto.ParPathDto
import com.practice.infra.service.cache.RedisService
import com.practice.infra.service.file.FileService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException

@Transactional(readOnly = true)
@Service
class UserService (
    private val userRepository: UserRepository,
    private val userCustomRepository: UserCustomRepository,
    private val redisService: RedisService,
    private val fileService: FileService
) {
    fun findUserByIdx(userIdx: Long): User {
        return userRepository.findById(userIdx)
            .orElseThrow { HttpClientErrorException(HttpStatus.valueOf(404), "NotFound User") }
    }

    fun findUserDetail(requestUser: CustomOAuth2User): UserDetailResponseDto? {
        val user = findUserByIdx(requestUser.userIdx())
        return user.imageUrl?.let { url ->
            if (url.startsWith("http")) {
                return UserDetailResponseDto.from(user, url)
            }
            return UserDetailResponseDto.from(user, profileImageParUrl(user.idx!!, user.imageUrl!!))
        } ?:let { UserDetailResponseDto.from(user) }
    }

    private fun profileImageParUrl(userIdx: Long, filePath: String): String {
        val cacheKey = CacheKey.OCI_USER_READ_KEY.generateKey(userIdx.toString(), FilePathRequestType.PROFILE.name)
        return redisService.get(cacheKey, ParPathDto::class.java)?.parUrl
            ?:let {
                val generateParReadUrl = fileService.generateParReadUrl(filePath)
                redisService.set(cacheKey, generateParReadUrl, CacheKey.OCI_USER_READ_KEY.expire())
                return generateParReadUrl.parUrl
            }
    }

    fun searchUsers(requestUser: CustomOAuth2User, requestDto: UserSearchRequestDto, pageable: Pageable): UserDetailsResponseDto {
        val searchDao = requestDto.toDao(requestUser.userIdx(), pageable)
        val users = userCustomRepository.findUsersBySearchDao(searchDao)

        return UserDetailsResponseDto.from(users)
    }

    @Transactional
    fun updateUserProfile(requestUser: CustomOAuth2User, requestDto: UserProfileUpdateRequestDto) {
        val user = findUserByIdx(requestUser.userIdx())

        if (user.validateUpdatableProfileImg(requestDto.profileImageUrl)) {
            val cacheKey = CacheKey.OCI_USER_READ_KEY.generateKey(requestUser.userIdx().toString(), FilePathRequestType.PROFILE.name)
            redisService.delete(cacheKey)
            fileService.deleteFile(user.imageUrl!!)
        }

        user.update(requestDto.nickname, requestDto.profileImageUrl)
    }
}