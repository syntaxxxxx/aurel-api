package com.aej.plugins

import com.aej.KoinContainer
import com.aej.MainException
import com.aej.repository.cart.Cart
import com.aej.repository.user.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*
import kotlin.collections.set

fun Application.configureSecurity() {

    val userRepository = KoinContainer.userRepository

    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
        basic("auth-basic") {
            realm = "Access API"
            validate { credential ->
                val userDb = userRepository.getUserByName(credential.name)
                val userRequest = User(name = credential.name, password = credential.password)

                println("request pas -> ${userRequest.password}")
                println("db pas -> ${userDb.password}")

                when {
                    userDb.password != userRequest.password -> {
                        throw MainException("Invalid token", HttpStatusCode.Unauthorized)
                    }
                    else -> UserIdPrincipal(credential.name)
                }
            }
        }
    }

    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}