package com.antoinecampbell.ktor

import io.ktor.server.config.ApplicationConfig

class DatasourceParams(
    val driver: String,
    val jdbcUrl: String,
    val username: String,
    val password: String
)

fun datasourceParams(config: ApplicationConfig) = DatasourceParams(
    driver = config.property("datasource.driver").getString(),
    jdbcUrl = config.property("datasource.jdbcUrl").getString(),
    username = config.property("datasource.username").getString(),
    password = config.property("datasource.password").getString()
)
