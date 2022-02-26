package com.aej.repository.user

import com.aej.MainException
import com.aej.screen.request.UserRequest
import com.aej.services.authentication.JwtConfig
import com.aej.utils.AESUtils
import com.aej.utils.randomString
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import java.time.Instant

data class User(
    var id: String = "",
    var username: String = "",
    var password: String = "",
    var role: Role = Role.CUSTOMER,
    var imageUrl: String = "",
    var fullName: String = "",
    var simpleAddress: String = "",
    var fcmToken: String = "",
    var fcmServerKey: String = "",
    var createdAt: String = "${Instant.now()}",
    var updatedAt: String = "${Instant.now()}"
) {

    companion object {
        fun of(userRequest: UserRequest, role: Role): User {
            val username = userRequest.username
            val password = userRequest.password
            val fullName = userRequest.fullName
            val passwordHash = AESUtils.encrypt(password)
            val hashId = randomString()
            val user = User(
                id = hashId,
                username = username,
                fullName = fullName,
                role = role
            )
            user.password = passwordHash
            return user
        }

        suspend fun fromToken(applicationRequest: ApplicationRequest, userRepository: UserRepository, role: Role? = null): User {
            val principal = applicationRequest.call.principal<JWTPrincipal>()
            val payload = principal?.payload
            val userId  = payload?.getClaim(JwtConfig.ID)?.asString().orEmpty()

            val user = userRepository.getUser(userId)
            if (role != null && role != user.role) throw MainException("User not allowed", HttpStatusCode.Forbidden)

            return user
        }
    }

    fun updateWith(updated: User): User {
        if (updated.fullName.isNotEmpty()) {
            fullName = updated.fullName
        }

        if (updated.simpleAddress.isNotEmpty()) {
            simpleAddress = updated.simpleAddress
        }

        if (updated.imageUrl.isNotEmpty()) {
            fullName = updated.imageUrl
        }

        if (updated.fcmToken.isNotEmpty()) {
            fullName = updated.fcmToken
        }

        updatedAt = "${Instant.now()}"
        return this
    }

    enum class Role {
        CUSTOMER, SELLER
    }
}