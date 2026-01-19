import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.25" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.9" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.25" apply false

}

group = "com.practice.socket"
version = "0.0.1-SNAPSHOT"
description = "websocket-kopring"

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

    extensions.configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.9")
        }
    }

    // Java toolchain
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    // Kotlin compiler options
    extensions.configure<KotlinJvmProjectExtension> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    // allOpen (JPA)
    extensions.configure<AllOpenExtension> {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("jakarta.persistence.Embeddable")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
