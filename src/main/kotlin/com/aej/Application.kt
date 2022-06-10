package com.aej

import com.aej.plugins.configureMonitoring
import com.aej.plugins.configureSerialization
import com.aej.plugins.configureStatusPage
import com.aej.plugins.*
import com.aej.plugins.routing.configureRouting
import com.aej.plugins.routing.configureRoutingV1
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {

    embeddedServer(Netty, port = System.getenv("PORT").toInt(), host = "0.0.0.0") {
        configureKoin()
        configureSecurity()
        configureStatusPage()
        configureDocumentation()
        configureRouting()
        configureRoutingV1()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
    }.start(wait = true)
}
