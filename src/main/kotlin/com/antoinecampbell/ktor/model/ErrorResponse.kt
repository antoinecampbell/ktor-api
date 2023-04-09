package com.antoinecampbell.ktor.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val message: String? = null,
    val code: String? = null,
)
