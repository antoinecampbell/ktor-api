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
import javax.sql.DataSource

private lateinit var dataSource: HikariDataSource

fun Application.configureDatabase(): DataSource {
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

    return dataSource
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = {
        block()
    })
