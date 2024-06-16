pluginManagement {
    val kotlinVersion: String by settings
    val ktorVersion: String by settings
    val ktLintVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        id("org.jlleitschuh.gradle.ktlint") version ktLintVersion
    }
}

rootProject.name = "ktor-api"
