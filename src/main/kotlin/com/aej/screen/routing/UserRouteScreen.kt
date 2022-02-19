package com.aej.screen.routing

import com.aej.KoinContainer
import com.aej.MainException
import com.aej.repository.cart.Cart
import com.aej.screen.response.MainResponse
import com.aej.repository.user.User
import com.aej.screen.request.UserFcmTokenRequest
import com.aej.screen.request.UserRequest
import com.aej.screen.response.UserResponse
import com.aej.services.image.ImageStorageServices
import com.aej.utils.AESUtils
import com.aej.utils.mapToResponse
import com.aej.utils.orRandom
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

object UserRouteScreen {
    private val userRepository = KoinContainer.userRepository
    private val cartRepository = KoinContainer.cartRepository

    suspend fun register(applicationCall: ApplicationCall, role: User.Role) = applicationCall.run {
        val userRequest = receive<UserRequest>()
        val user = User.of(userRequest.name, userRequest.password, role)
        userRepository.createUser(user)

        if (role == User.Role.CUSTOMER) {
            val cart = Cart.of(user)
            cartRepository.createCart(cart)
        }
        val token = Base64.getEncoder().encodeToString("${user.name}:${user.password}".toByteArray())
        val data = mapOf("token" to "Basic $token")
        respond(MainResponse.bindToResponse(data, "Create user"))
    }

    suspend fun login(applicationCall: ApplicationCall) = applicationCall.run {
        val userRequest = receive<UserRequest>()
        val user = userRepository.getUser(User.getIdByName(userRequest.name))

        val passwordRequest = userRequest.password
        val passwordDb = AESUtils.decrypt(user.password).replace("\"", "")

        if (passwordRequest != passwordDb) throw MainException("Password invalid!", HttpStatusCode.Unauthorized)

        val token = Base64.getEncoder().encodeToString("${user.name}:${user.password}".toByteArray())
        val data = mapOf("token" to "Basic $token")
        respond(MainResponse.bindToResponse(data, "Login"))
    }

    suspend fun getUser(applicationCall: ApplicationCall) = applicationCall.run {
        val id = parameters["id"].orEmpty()
        if (id.isNotEmpty()) {
            val user = userRepository.getUser(id).mapToResponse()
            val response = MainResponse.bindToResponse(user, "User")
            respond(response)
        } else {
            val user = User.fromToken(request, userRepository).mapToResponse()
            respond(MainResponse.bindToResponse(user, "User"))
        }
    }

    suspend fun updateFcmToken(applicationCall: ApplicationCall) = applicationCall.run {
        val fcmRequest = receive<UserFcmTokenRequest>()
        val user = User.fromToken(request, userRepository)
        user.fcmToken = fcmRequest.fcmToken
        userRepository.updateUser(user)
        respond(MainResponse.bindToResponse(user.mapToResponse(), "Update fcm token"))
    }

    suspend fun updateProfileUser(applicationCall: ApplicationCall) = applicationCall.run {
        val userRequest = receive<User>()
        val user = User.fromToken(request, userRepository)
        val userUpdated = user.updateWith(userRequest)
        userRepository.updateUser(userUpdated)
        val userFromDb = userRepository.getUser(user.id)
        respond(MainResponse.bindToResponse(userFromDb.mapToResponse(), "Update profile"))
    }

    suspend fun updateImageProfile(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        receiveMultipart().readAllParts().onEach { part ->
            when (part) {
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    val baseUrl = "https://aurel-store.herokuapp.com"

                    val urlImage = ImageStorageServices.uploadFile(fileBytes, part.originalFileName.orRandom(), user.name, baseUrl)
                    user.imageUrl = urlImage
                }
                else -> {}
            }
        }

        userRepository.updateUser(user)
        val userFromDb = userRepository.getUser(user.id)
        respond(MainResponse.bindToResponse(userFromDb.mapToResponse(), "Update image profile"))
    }
}