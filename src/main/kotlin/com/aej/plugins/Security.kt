package com.aej.plugins

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.services.authentication.JwtConfig
import com.aej.utils.orNol
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureSecurity() {

    val userRepository = KoinContainer.userRepository
    val jwtConfig = KoinContainer.jwtConfig

    install(Authentication) {
        jwt(JwtConfig.NAME) {
            realm = JwtConfig.AUTH_REALM
            verifier(jwtConfig.verifier)

            validate { jwtCredential ->
                val payload = jwtCredential.payload
                val userIdInToken = payload.getClaim(JwtConfig.ID).asString().orEmpty()
                val passwordHashInToken = payload.getClaim(JwtConfig.HASH).asString().orEmpty()
                val expiredAt = jwtCredential.expiresAt?.time?.minus(System.currentTimeMillis()).orNol()

                val user = userRepository.getUser(userIdInToken)
                val userIsValid = userIdInToken == user.id && passwordHashInToken == user.password
                val tokenIsExpired = expiredAt <= 0

                when {
                    tokenIsExpired -> throw MainException("Token is expired", HttpStatusCode.Unauthorized)
                    userIsValid -> JWTPrincipal(payload)
                    else -> throw MainException("Invalid token", HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}