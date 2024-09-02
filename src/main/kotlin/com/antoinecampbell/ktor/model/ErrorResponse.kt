package com.antoinecampbell.ktor.model

data class ErrorResponse(
    val message: String? = null,
    val code: String? = null,
)
