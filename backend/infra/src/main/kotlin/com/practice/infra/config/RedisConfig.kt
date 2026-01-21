package com.practice.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.infra.config.properties.RedisConnectionProperties
import io.lettuce.core.ClientOptions
import io.lettuce.core.SocketOptions
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@EnableCaching
@Configuration
class RedisConfig (
    private val connectionProperties: RedisConnectionProperties
) {
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory, redisObjectMapper: ObjectMapper): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer(redisObjectMapper)
        return redisTemplate
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val clientOption: ClientOptions = ClientOptions.builder()
            .socketOptions(SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(connectionProperties.timeout.connection))
                .build())
            .build()

        val lettuceClientConfiguration: LettuceClientConfiguration = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(connectionProperties.timeout.command))
            .shutdownTimeout(Duration.ofMillis(connectionProperties.timeout.shutdown))
            .clientOptions(clientOption)
            .build()

        val redisConfiguration = RedisStandaloneConfiguration()
        redisConfiguration.hostName = connectionProperties.host
        redisConfiguration.port = connectionProperties.port

        return LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration)
    }
}