package com.antoinecampbell.ktor.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/items") {
            call.respond(
                Item(
                    id = 1,
                    name = "test",
                    timestamp = Instant.now(),
                    date = LocalDate.now(),
                    zonedTimestamp = ZonedDateTime.now()
                )
            )
        }
        post("/items") {
            val item = call.receive<Item>()
            call.application.environment.log.debug("Item: {}", item)
            call.respond(item)
        }
    }
}

data class Item(
    val id: Int?,
    val name: String,
    val timestamp: Instant,
    val date: LocalDate,
    val zonedTimestamp: ZonedDateTime
)
