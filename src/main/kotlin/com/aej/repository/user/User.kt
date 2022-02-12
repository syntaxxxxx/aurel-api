package com.aej.repository.user

import com.aej.MainException
import com.aej.utils.AESUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.http.*
import io.ktor.server.request.*
import java.util.*

data class User(
    var id: String = "",
    var name: String = "",
    var password: String = "",
    var role: Role = Role.CUSTOMER,
    var fcmToken: String = "",
    var fcmServerKey: String = ""
) {

    fun generateId(): User {
        id = AESUtils.encrypt(name).take(8)
        return this
    }

    companion object {
        fun of(name: String, password: String, role: Role): User {
            val passwordHash = AESUtils.encrypt(password)
            val hashId = AESUtils.encrypt(name).take(8)
            val user = User(
                id = hashId,
                name = name,
                role = role
            )
            user.password = passwordHash
            return user
        }

        fun getIdByName(name: String): String {
            return AESUtils.encrypt(name).take(8)
        }

        fun fromStringRaw(string: String): User {
            val type = object : TypeToken<User>() {}.type
            return try {
                Gson().fromJson(string, type)
            } catch (e: Throwable) {
                throw MainException("User not found", HttpStatusCode.NotFound)
            }
        }

        fun fromStringRawNullable(string: String): User? {
            val type = object : TypeToken<User>() {}.type
            return try {
                Gson().fromJson(string, type)
            } catch (e: Throwable) {
                null
            }
        }

        suspend fun fromToken(applicationRequest: ApplicationRequest, userRepository: UserRepository, role: Role? = null): User {
            val key = applicationRequest.headers["Authorization"]?.removePrefix("Basic ").orEmpty()
            if (key.isEmpty()) throw MainException("Token invalid", HttpStatusCode.Unauthorized)

            val decryptKeyByteArray = Base64.getDecoder().decode(key)
            val decryptKey = String(decryptKeyByteArray)
            val params = decryptKey.split(":")

            val user = userRepository.getUserByName(params[0])
            if (role != null && role != user.role) throw MainException("User not allowed", HttpStatusCode.Forbidden)

            return user
        }
    }

    enum class Role {
        CUSTOMER, SELLER
    }
}