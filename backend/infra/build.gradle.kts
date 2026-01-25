plugins {
    id("org.jetbrains.kotlin.plugin.spring")
    id("java-library")
}

dependencies {
    implementation(project(":backend:common"))

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // oci
    implementation("com.oracle.oci.sdk:oci-java-sdk-objectstorage:3.77.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-jersey3:3.77.0")
}