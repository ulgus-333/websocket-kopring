package com.practice.infra.service.file

import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse
import com.practice.infra.config.properties.OciProperties
import com.practice.infra.domain.dto.CacheKey
import com.practice.infra.domain.dto.PARCacheDto
import com.practice.infra.domain.dto.ParPathDto
import com.practice.infra.domain.type.FilePathType
import com.practice.infra.service.cache.RedisService
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

    fun generateParWriteUrl(pathType: FilePathType, fileName: String, cacheKey: String, variables: List<String>): ParPathDto {
        val expireAt = Date.from(Instant.now().plus(OciProperties.WRITE_EXPIRE_DURATION))
        val filePath = pathType.generateFilePath(*variables.toTypedArray()) + fileName

        val writeUrl = generateParUrl(filePath, WRITE_ACCESS_TYPE, expireAt)

        val parIdCacheKey = CacheKey.OCI_PAR_KEY.generateKey(pathType.name, cacheKey, fileName)
        redisService.set(parIdCacheKey, PARCacheDto.from(writeUrl.preauthenticatedRequest), CacheKey.OCI_PAR_KEY.expire())

        return ParPathDto(
            parUrl = ociProperties.preAuthenticatedRequestUrl(writeUrl.preauthenticatedRequest.accessUri),
            filePath = filePath,
            fileName = fileName
        )
    }

    fun generateParReadUrl(filePath: String): ParPathDto {
        val expireAt = Date.from(Instant.now().plus(OciProperties.READ_EXPIRE_DURATION))

        val readUrl = generateParUrl(filePath, READ_ACCESS_TYPE, expireAt)

        return ParPathDto(
            parUrl = ociProperties.preAuthenticatedRequestUrl(readUrl.preauthenticatedRequest.accessUri),
            filePath = filePath,
            fileName = filePath.substringAfterLast("/")
        )
    }

    fun expireRemainPar(pathType: FilePathType, fileName: String, cacheKey: String) {
        val parIdCacheKey = CacheKey.OCI_PAR_KEY.generateKey(pathType.name, cacheKey, fileName)

        redisService.get(parIdCacheKey, PARCacheDto::class.java)
            ?.let {
                val request = DeletePreauthenticatedRequestRequest.builder()
                    .namespaceName(ociProperties.namespace)
                    .bucketName(ociProperties.bucket)
                    .parId(it.parRequestId)
                    .build()
                objectStorage.deletePreauthenticatedRequest(request)
                redisService.delete(parIdCacheKey)
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

    private fun generateParUrl(filePath: String, accessType: String, expireAt: Date): CreatePreauthenticatedRequestResponse {
        val details = CreatePreauthenticatedRequestDetails.builder()
            .name("par-" + UUID.randomUUID())
            .objectName(filePath)
            .accessType(AccessType.create(accessType))
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