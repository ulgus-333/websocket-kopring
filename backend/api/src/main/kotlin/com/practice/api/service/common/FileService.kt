package com.practice.api.service.common

import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse
import com.practice.api.config.properties.OciProperties
import com.practice.api.domain.dto.CustomOAuth2User
import com.practice.api.domain.presentation.request.file.PARRequestDto
import com.practice.api.domain.presentation.response.file.FileResponseDto
import com.practice.api.service.common.dto.CacheKey
import com.practice.api.service.common.dto.PARCacheDto
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class FileService (
    private val objectStorage: ObjectStorage,
    private val ociProperties: OciProperties,
    private val redisService: RedisService
) {
    companion object {
        private const val READ_ACCESS_TYPE = "ObjectRead"
        private const val WRITE_ACCESS_TYPE = "ObjectWrite"
    }

    fun generateParWriteUrl(requestUser: CustomOAuth2User, requestDto: PARRequestDto): FileResponseDto {
        val expireAt = Date.from(Instant.now().plus(OciProperties.WRITE_EXPIRE_DURATION))
        val filePath = requestDto.generateFilePath(requestUser.userIdx().toString())

        val response = generatePar(filePath, WRITE_ACCESS_TYPE, expireAt)

        val parUrl = ociProperties.preAuthenticatedRequestUrl(response.preauthenticatedRequest.accessUri)

        val cacheKey = CacheKey.OCI_PAR_KEY.generateKey(requestDto.pathType.name, requestUser.userIdx().toString())
        redisService.set(cacheKey, PARCacheDto.from(response.preauthenticatedRequest), CacheKey.OCI_PAR_KEY.expire())

        return FileResponseDto(
            parUrl = parUrl,
            filePath = filePath,
            fileName = requestDto.filename
        )
    }

    fun generateParReadUrl(filePath: String): String {
        val expireAt = Date.from(Instant.now().plus(OciProperties.READ_EXPIRE_DURATION))
        val response = generatePar(filePath, READ_ACCESS_TYPE, expireAt)

        return ociProperties.preAuthenticatedRequestUrl(response.preauthenticatedRequest.accessUri)
    }

    fun expireRemainPAR(requestUser: CustomOAuth2User, requestDto: PARRequestDto) {
        val cacheKey = CacheKey.OCI_PAR_KEY.generateKey(requestDto.pathType.name, requestUser.userIdx().toString())

        redisService.get(cacheKey, PARCacheDto::class.java)
            ?.let {
                val request = DeletePreauthenticatedRequestRequest.builder()
                    .bucketName(ociProperties.bucket)
                    .namespaceName(ociProperties.namespace)
                    .parId(it.parRequestId)
                    .build()

                objectStorage.deletePreauthenticatedRequest(request)
                redisService.delete(cacheKey)
            }
    }

    fun deleteFile(filePath: String) {
        val request = DeleteObjectRequest.builder()
            .namespaceName(ociProperties.namespace)
            .bucketName(ociProperties.bucket)
            .objectName(filePath)
            .build()
        objectStorage.deleteObject(request)
    }

    private fun generatePar(filePath: String, accessType: String, expireAt: Date): CreatePreauthenticatedRequestResponse {
        val details = CreatePreauthenticatedRequestDetails.builder()
            .name("par-" + UUID.randomUUID())
            .objectName(filePath)
            .accessType(AccessType.valueOf(accessType))
            .timeExpires(expireAt)
            .build()

        val request = CreatePreauthenticatedRequestRequest.builder()
            .namespaceName(ociProperties.namespace)
            .bucketName(ociProperties.bucket)
            .createPreauthenticatedRequestDetails(details)
            .build()

        return objectStorage.createPreauthenticatedRequest(request)
    }
}
