package com.antoinecampbell.ktor.plugins

import com.antoinecampbell.ktor.datasourceParams
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

lateinit var dataSource: HikariDataSource

fun Application.configureDatabase() {
    val params = datasourceParams(environment.config)
    val config = HikariConfig().apply {
        driverClassName = params.driver
        poolName = "ktor-api"
        jdbcUrl = params.jdbcUrl
        maximumPoolSize = 10
        minimumIdle = 10
        username = params.username
        password = params.password
    }
    dataSource = HikariDataSource(config)

    // Start Flyway migrations
    Flyway.configure().dataSource(dataSource)
        .load()
        .migrate()

    // Configure Exposed
    Database.connect(dataSource)
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = {
        block()
    })
