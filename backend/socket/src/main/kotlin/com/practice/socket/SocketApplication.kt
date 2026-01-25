package com.practice.socket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.practice.socket", "com.practice.common", "com.practice.infra"])
class SocketApplication

fun main(args: Array<String>) {
    runApplication<SocketApplication>(*args)
}
