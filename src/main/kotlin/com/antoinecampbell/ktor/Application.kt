package com.antoinecampbell.ktor

import com.antoinecampbell.ktor.model.ErrorResponse
import com.antoinecampbell.ktor.plugins.configureMetrics
import com.antoinecampbell.ktor.plugins.configureRouting
import com.antoinecampbell.ktor.plugins.configureSerialization
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureMetrics()
    install(CallLogging)
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
    environment.config
}
