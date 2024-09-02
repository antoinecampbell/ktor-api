package com.antoinecampbell.ktor.note

import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZonedDateTime

data class Note(
    val id: Int?,
    val name: String,
    val timestamp: Instant?,
    val date: LocalDate?,
    val zonedTimestamp: ZonedDateTime?,
    val offsetTimestamp: OffsetDateTime?,
)
