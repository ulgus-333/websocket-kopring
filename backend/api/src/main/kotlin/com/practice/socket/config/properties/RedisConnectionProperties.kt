package com.practice.socket.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
@ConfigurationProperties(prefix = "redis")
class RedisConnectionProperties {
    lateinit var host: String
    var port by Delegates.notNull<Int>()
    lateinit var timeout: Timeout

    data class Timeout (
        var connection: Long,
        var command: Long,
        var shutdown: Long
    )
}