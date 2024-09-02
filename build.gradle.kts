val development: String by project
val environment: String by project
val ktorVersion: String by project
val kotlinVersion: String by project
val jacksonVersion: String by project
val arrowKtVersion: String by project
val koinVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    jvmToolchain(21)
}

group = "com.antoinecampbell.ktor"
application {
    mainClass.set("com.antoinecampbell.ktor.ApplicationKt")

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
    implementation(platform("io.ktor:ktor-bom:$ktorVersion"))
    // Server
    implementation("io.ktor:ktor-server-netty-jvm")
    // Server plugins
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-config-yaml")
    // Serialization
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.7")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    // Monitoring
    implementation("io.ktor:ktor-server-metrics-micrometer")
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.5")
    // Arrow
    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowKtVersion")
    implementation("io.arrow-kt:suspendapp:0.4.0")

    // Koin
//    implementation(platform("io.insert-koin:koin-bom:$koinVersion"))
//    implementation("io.insert-koin:koin-ktor")
//    implementation("io.insert-koin:koin-logger-slf4j")

    // Database
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core:10.17.2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.17.2")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.h2database:h2:2.3.232")

    // ORM
    implementation(platform("org.jetbrains.exposed:exposed-bom:0.54.0"))
    implementation("org.jetbrains.exposed:exposed-jdbc")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-java-time")
    implementation("org.jetbrains.exposed:exposed-json")
    runtimeOnly("org.jetbrains.exposed:exposed-dao")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
}
