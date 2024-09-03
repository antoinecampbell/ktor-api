package com.antoinecampbell.ktor

import com.antoinecampbell.ktor.plugins.configureDatabase
import com.antoinecampbell.ktor.plugins.configureRouting
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals

class ApplicationTest : StringSpec({
    "test root" {
        testApplication {
            application {
                configureRouting()
                configureDatabase()
            }
            client.get("/").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Hello World!", bodyAsText())
            }
        }
    }
})
