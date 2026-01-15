package com.practice.socket.config

import com.oracle.bmc.ConfigFileReader
import com.oracle.bmc.auth.AuthenticationDetailsProvider
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import com.practice.socket.config.properties.OciProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OciConfig (
    private val ociProperties: OciProperties
) {
    @Bean
    fun authenticationDetailsProvider(): AuthenticationDetailsProvider
        = ConfigFileAuthenticationDetailsProvider(ConfigFileReader.parseDefault())

    @Bean
    fun objectStorage(authenticationDetailsProvider: AuthenticationDetailsProvider): ObjectStorage
        = ObjectStorageClient.builder()
            .region(ociProperties.region)
            .build(authenticationDetailsProvider)
}