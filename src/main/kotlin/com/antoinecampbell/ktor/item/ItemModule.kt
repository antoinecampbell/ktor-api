package com.antoinecampbell.ktor.item

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

private val logger = KotlinLogging.logger { }

fun Application.configureItemModule(
    daoRepository: DaoItemRepository,
    tableRepository: TableItemRepository
) {
    routing {
        // Dao endpoints
        get("/items") {
            call.respond(daoRepository.findAll())
        }
        get("/items/{id}") {
            val param = call.parameters["id"]
            val id = param?.toIntOrNull() ?: throw BadRequestException("Invalid id: $param")
            val item = daoRepository.findById(id) ?: throw NotFoundException("No item with id: $id")
            call.respond(item)
        }
        post("/items") {
            val item = call.receive<Item>()
            val newItem = daoRepository.save(item)
            logger.debug { "Item create: $newItem" }
            call.respond(newItem)
        }
        // Table endpoints
        get("/items2") {
            call.respond(tableRepository.findAll())
        }
        get("/items2/{id}") {
            val param = call.parameters["id"]
            val id = param?.toIntOrNull() ?: throw BadRequestException("Invalid id: $param")
            val item = tableRepository.findById(id) ?: throw NotFoundException("No item with id: $id")
            call.respond(item)
        }
        post("/items2") {
            val item = call.receive<Item>()
            val newItem = tableRepository.save(item)
            logger.debug { "Item create: $newItem" }
            call.respond(newItem)
        }
    }
}
