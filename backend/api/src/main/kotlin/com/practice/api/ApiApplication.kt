package com.practice.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.practice.common", "com.practice.api", "com.practice.infra"])
@EnableJpaRepositories(basePackages = ["com.practice.common.repository"])
@EntityScan(basePackages = ["com.practice.common.domain.entity"])
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
