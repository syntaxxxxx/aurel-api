package com.aej.plugins

import io.ktor.server.application.*
import me.hana.docs.HanaDocs

fun Application.configureDocumentation() {
    install(HanaDocs) {
        title = "Aurel"
        path = "/docs"
        host = "https://aurel-store.herokuapp.com"
        postman = "https://www.postman.com/kucingapes/workspace/utsman-workspace/request/3885530-ab2a37c1-cd1c-4493-9f24-e0aba2652781"
        description = """
            Aurel is mock marketplace with real test payment by Xendit.
        """.trimIndent()
    }
}