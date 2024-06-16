val development: String by project
val environment: String by project
val ktorVersion: String by project
val kotlinVersion: String by project
val jacksonVersion: String by project
val arrowKtVersion: String by project

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
    //
    // implementation("io.ktor:ktor-server-core-jvm")
    // implementation("io.ktor:ktor-server-host-common-jvm")
    // implementation("io.ktor:ktor-server-status-pages-jvm")
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
    // implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")
    // Monitoring
    implementation("io.ktor:ktor-server-metrics-micrometer")
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.5")
    // Arrow
    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowKtVersion")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
}
