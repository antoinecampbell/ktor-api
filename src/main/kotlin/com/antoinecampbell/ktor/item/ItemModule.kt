package com.antoinecampbell.ktor.item

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureItemModule(repository: ItemRepository) {
    routing {
        get("/items") {
            call.respond(repository.findAll())
        }
        post("/items") {
            val item = call.receive<Item>()
            val newItem = repository.save(item)
            call.application.environment.log.debug("Item: {}", newItem)
            call.respond(newItem)
        }
    }
}
