package com.practice.api.service.common.dto

import java.time.Duration

enum class CacheKey (
    private val prefix: CacheKeyPrefix,
    private val keyName: String,
    private val parameterCount: Int,
    private val expire: Duration
) {
    OCI_PAR_KEY(CacheKeyPrefix.OCI, "PAR::%s::%s", 2, Duration.ofMinutes(5)),
    OCI_USER_READ_KEY(CacheKeyPrefix.OCI, "PAR::READ::%s::%s", 2, Duration.ofDays(1)),
    ;

    fun generateKey(vararg keyParams: String?): String {
        if (parameterCount == 0) {
            return prefix.name + keyName
        }
        require(parameterCount == keyParams.size) { "Invalid number of parameters" }

        return prefix.name + String.format(keyName, *keyParams)
    }

    fun expire(): Duration = this.expire
}