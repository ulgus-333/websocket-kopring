package com.practice.socket.service.common

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService (
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(RedisService::class.java)
    private val valueOps = redisTemplate.opsForValue()

    fun <T> get(key: String, clazz: Class<T>): T? {
        try {
            return valueOps.get(key)?.let { clazz.cast(it) }
        } catch (e: Exception) {
            logger.error(e.message, e)
            return null
        }
    }

    fun set(key: String, value: Any, expire: Duration): Boolean {
        require(key.isNotBlank()) { "Key cannot be blank" }
        try {
            valueOps.set(key, value, expire)
            return true
        } catch (e: Exception) {
            logger.error(e.message, e)
            return false
        }
    }

    fun delete(key: String): Boolean {
        require(key.isNotBlank()) { "Key cannot be blank" }
        return redisTemplate.delete(key)
    }
}