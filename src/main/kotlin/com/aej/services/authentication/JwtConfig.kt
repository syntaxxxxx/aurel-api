package com.aej.services.authentication

import com.aej.repository.user.User
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtConfig {

    companion object {
        private const val SECRET = "utsman"
        private const val ISSUER = "com.aej.aurel"
        private const val SUBJECT = "Authentication"
        private const val VALIDITY = (36_000_00 * 24) * 7 // seminggu
        const val NAME = "auth-jwt"
        const val ID = "id"
        const val HASH = "hash"
        const val AUTH_REALM = "Access to 'v1'"
    }

    private val algorithm = Algorithm.HMAC512(SECRET)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .build()

    private fun expired() = Date(System.currentTimeMillis() + VALIDITY)

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(SUBJECT)
            .withIssuer(ISSUER)
            .withClaim(ID, user.id)
            .withClaim(HASH, user.password)
            .withExpiresAt(expired())
            .sign(algorithm)
    }
}