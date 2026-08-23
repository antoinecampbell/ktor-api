package com.antoinecampbell.ktor

import io.kotest.core.spec.style.StringSpec
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ConfigLoader
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals

class ApplicationTest :
    StringSpec({
        "test root" {
            testApplication {
                environment {
                    config = ConfigLoader.load()
                }
                client.get("/").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals("Hello World!", bodyAsText())
                }
            }
        }
    })
