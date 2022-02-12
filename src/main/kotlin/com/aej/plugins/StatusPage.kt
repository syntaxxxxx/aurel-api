package com.aej.plugins

import com.aej.MainException
import com.aej.screen.response.MainResponse
import com.mongodb.MongoCommandException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

fun Application.configureStatusPage() {
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                MainResponse(
                    status = false,
                    code = HttpStatusCode.Unauthorized.value,
                    message = "Invalid token"
                )
            )
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                MainResponse(
                    status = false,
                    code = HttpStatusCode.NotFound.value,
                    message = "No route found, please check the documentation.."
                )
            )
        }
        exception<MainException> { call, cause ->
            call.respond(
                cause.code,
                MainResponse(
                    status = false,
                    code = cause.code.value,
                    message = cause.localizedMessage
                )
            )
        }
        exception<IllegalStateException> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                MainResponse(
                    status = false,
                    code = HttpStatusCode.InternalServerError.value,
                    message = cause.localizedMessage
                )
            )
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                MainResponse(
                    status = false,
                    code = HttpStatusCode.InternalServerError.value,
                    message = cause.localizedMessage + ". Maybe try again"
                )
            )
        }
        exception<MongoCommandException> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                MainResponse(
                    status = false,
                    code = HttpStatusCode.InternalServerError.value,
                    message = cause.localizedMessage + ". Maybe try again"
                )
            )
        }
    }
}