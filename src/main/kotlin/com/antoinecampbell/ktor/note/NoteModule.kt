package com.antoinecampbell.ktor.note

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

fun Application.configureNoteModule(repository: NoteRepository) {
    routing {
        get("/notes") {
            call.respond(repository.findAll())
        }
        get("/notes/{id}") {
            val param = call.parameters["id"]
            val id = param?.toIntOrNull() ?: throw BadRequestException("Invalid id: $param")
            val note = repository.findById(id) ?: throw NotFoundException("No note with id: $id")
            call.respond(note)
        }
        post("/notes") {
            val note = call.receive<Note>()
            val newNote = repository.save(note)
            logger.debug { "Note created: $newNote" }
            call.respond(newNote)
        }
    }
}
