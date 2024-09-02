package com.antoinecampbell.ktor

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.antoinecampbell.ktor.item.DaoItemRepository
import com.antoinecampbell.ktor.item.TableItemRepository
import com.antoinecampbell.ktor.item.configureItemModule
import com.antoinecampbell.ktor.model.ErrorResponse
import com.antoinecampbell.ktor.note.DefaultNoteRepository
import com.antoinecampbell.ktor.note.configureNoteModule
import com.antoinecampbell.ktor.plugins.configureDatabase
import com.antoinecampbell.ktor.plugins.configureMetrics
import com.antoinecampbell.ktor.plugins.configureRouting
import com.antoinecampbell.ktor.plugins.configureSerialization
import com.antoinecampbell.ktor.plugins.configureSwagger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.ConfigLoader
import io.ktor.server.config.ConfigLoader.Companion.load
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.coroutines.awaitCancellation
import org.slf4j.event.Level
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) = SuspendApp {
    resourceScope {
        EngineMain.main(args)
        awaitCancellation()
    }
}

fun Application.module() {
    val appConfig = ConfigLoader.load()
    logger.debug { "Config: ${appConfig.toMap()}" }
    configureSerialization()
    configureRouting()
    configureMetrics()
    configureSwagger()
    if (environment.developmentMode) {
        install(CallLogging) {
            level = Level.DEBUG
        }
    }
    install(Compression) {
        gzip()
        deflate()
    }
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.warn(cause) { "Bad Request" }
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = cause.message))
        }
        exception<NotFoundException> { call, cause ->
            logger.debug(cause) { "Not Found" }
            call.respond(HttpStatusCode.NotFound, ErrorResponse(message = cause.message))
        }
        exception<Throwable> { call, cause ->
            logger.error(cause) { "Unknown Error" }
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(message = cause.message))
        }
    }

    val dataSource = configureDatabase()
    val dependencies = Dependencies(dataSource)
    configureItemModule(dependencies.daoItemRepository, dependencies.tableItemRepository)
    configureNoteModule(dependencies.notesRepository)
}

class Dependencies(dataSource: DataSource) {
    val daoItemRepository = DaoItemRepository()
    val tableItemRepository = TableItemRepository()
    val notesRepository = DefaultNoteRepository(dataSource)
}
