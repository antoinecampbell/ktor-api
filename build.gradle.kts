plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(25)
}

group = "com.antoinecampbell.ktor"
application {
    mainClass.set("com.antoinecampbell.ktor.ApplicationKt")

    val development = providers.gradleProperty("development").orElse("true").get()
    val args = mutableListOf("-Dio.ktor.development=$development")
    if (development.toBoolean()) {
        args += "-Dlogback.configurationFile=logback-local.xml"
    }
    applicationDefaultJvmArgs = args
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor BOM
    implementation(platform(libs.ktor.bom))
    // Server
    implementation(libs.ktor.server.netty)
    // Server plugins
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.swagger)
    // OpenAPI / Swagger
    // implementation("io.bkbn:kompendium-core:3.14.4")
    // Serialization
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.jackson.datatype.jsr310)
    // Logging
    implementation(libs.kotlin.logging)
    implementation(libs.logback.classic)
    implementation(libs.logstash.logback.encoder)
    // Monitoring
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    // Arrow
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.suspendapp)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Database
    implementation(libs.hikari)
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)
    implementation(libs.postgresql)
    implementation(libs.h2)

    // Exposed ORM
    implementation(platform(libs.exposed.bom))
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.json)
    runtimeOnly(libs.exposed.dao)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.testcontainers.postgresql)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("koverHtmlReport", "koverXmlReport")
}
