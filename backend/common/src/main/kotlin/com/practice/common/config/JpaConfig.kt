package com.practice.common.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Component

@EnableJpaRepositories(basePackages = ["com.practice.common.repository"])
@EntityScan(basePackages = ["com.practice.common.domain.entity"])
@Component
class JpaConfig {
}