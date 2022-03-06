package com.aej.screen.routing

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.repository.cart.Cart
import com.aej.screen.response.MainResponse
import com.aej.repository.user.User
import com.aej.screen.request.UserFcmTokenRequest
import com.aej.screen.request.UserRequest
import com.aej.services.image.ImageStorageServices
import com.aej.utils.AESUtils
import com.aej.utils.DefaultImageUtils
import com.aej.utils.mapToResponse
import com.aej.utils.orRandom
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object UserRouteScreen {
    private const val BASE_URL = "https://aurel-store.herokuapp.com"

    private val jwtConfig = KoinContainer.jwtConfig
    private val userRepository = KoinContainer.userRepository
    private val cartRepository = KoinContainer.cartRepository

    suspend fun register(applicationCall: ApplicationCall, role: User.Role) = applicationCall.run {
        val userRequest = receive<UserRequest>()
        val user = User.of(userRequest, role)

        val defaultImageBytes = DefaultImageUtils.createImage(user.username.first().toString())
        val defaultNames = "${user.username}-default.png"
        val urlImage = ImageStorageServices.uploadFile(
            defaultImageBytes,
            defaultNames,
            user.username,
            BASE_URL
        )
        user.imageUrl = urlImage
        userRepository.createUser(user)

        if (role == User.Role.CUSTOMER) {
            val cart = Cart.of(user)
            cartRepository.createCart(cart)
        }

        val token = jwtConfig.generateToken(user)
        val data = mapOf("token" to "Bearer $token")
        respond(MainResponse.bindToResponse(data, "Create user"))
    }

    suspend fun login(applicationCall: ApplicationCall) = applicationCall.run {
        val userRequest = receive<UserRequest>()

        val passwordHash = AESUtils.encrypt(userRequest.password)
        val user = userRepository.getUserByName(userRequest.username)
        val isUserValid = passwordHash == user.password

        if (isUserValid) {
            val token = jwtConfig.generateToken(user)
            val data = hashMapOf("token" to "Bearer $token")
            respond(MainResponse.bindToResponse(data, "Login token"))
        } else {
            throw MainException("Password invalid!", HttpStatusCode.Unauthorized)
        }
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

                    val urlImage = ImageStorageServices.uploadFile(
                        fileBytes,
                        part.originalFileName.orRandom(),
                        user.username,
                        BASE_URL
                    )
                    user.imageUrl = urlImage
                }
                else -> {}
            }
        }

        userRepository.updateUser(user)
        val userFromDb = userRepository.getUser(user.id)
        respond(MainResponse.bindToResponse(userFromDb.mapToResponse(), "Update image profile"))
    }

    suspend fun getSellerById(applicationCall: ApplicationCall) = applicationCall.run {
        val id = parameters["id"].orEmpty()
        println("seller id ------> $id")
        val user = userRepository.getUser(id)
        if (user.role != User.Role.SELLER) throw MainException("Seller not found")

        val userResponse = user.mapToResponse()
        respond(MainResponse.bindToResponse(userResponse, "Get seller profile"))
    }
}