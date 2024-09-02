package com.antoinecampbell.ktor

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.antoinecampbell.ktor.item.configureItemModule
import com.antoinecampbell.ktor.model.ErrorResponse
import com.antoinecampbell.ktor.plugins.configureDatabase
import com.antoinecampbell.ktor.plugins.configureMetrics
import com.antoinecampbell.ktor.plugins.configureRouting
import com.antoinecampbell.ktor.plugins.configureSerialization
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.config.ConfigLoader
import io.ktor.server.config.ConfigLoader.Companion.load
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.coroutines.awaitCancellation
import org.slf4j.event.Level

fun main(args: Array<String>) = SuspendApp {
    resourceScope {
        EngineMain.main(args)
        awaitCancellation()
    }
}

fun Application.module() {
    val appConfig = ConfigLoader.load()
    log.info("Config: {}", appConfig.toMap())
    configureSerialization()
    configureRouting()
    configureMetrics()
    install(CallLogging) {
        level = Level.DEBUG
    }
    install(Compression) {
        gzip()
        deflate()
    }
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.application.environment.log.warn("Bad Request", cause)
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = cause.message))
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unknown Error", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(message = cause.message))
        }
    }
    configureItemModule()
    configureDatabase()
}
