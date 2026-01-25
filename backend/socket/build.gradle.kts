plugins {
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
}


dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:infra"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
}