package com.practice.socket.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@ConfigurationProperties(prefix = "oci")
class OciProperties {
    lateinit var namespace: String
    lateinit var region: String
    lateinit var bucket: String

    fun preAuthenticatedRequestUrl(accessUri: String): String = String.format(URL_PREFIX, region, accessUri)

    companion object {
        val WRITE_EXPIRE_DURATION = Duration.ofMinutes(5)
        val READ_EXPIRE_DURATION = Duration.ofDays(1)
        private const val URL_PREFIX = "https://objectstorage.%s.oraclecloud.com%s"
    }
}
