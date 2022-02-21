package com.aej.screen.routing

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.repository.cart.Cart
import com.aej.repository.user.User
import com.aej.screen.request.CartRequest
import com.aej.screen.response.MainResponse
import com.aej.utils.mapToResponse
import com.aej.utils.orNol
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object CartRouteScreen {
    private val userRepository = KoinContainer.userRepository
    private val productRepository = KoinContainer.productRepository
    private val cartRepository = KoinContainer.cartRepository

    suspend fun getCart(applicationCall: ApplicationCall) = applicationCall.run {
        val cartData = getCartForUser(request).mapToResponse()
        respond(MainResponse.bindToResponse(cartData, "Get cart"))
    }

    suspend fun modifiedProductToCartMultipart(applicationCall: ApplicationCall, method: Method) = applicationCall.run {
        val user = getUser(request)
        val cardRequest = mutableListOf<CartRequest>()

        receiveMultipart().readAllParts().onEach { part ->
            when (part is PartData.FormItem) {
                true -> {
                    val productId = part.name.orEmpty()
                    val quantity = part.value.toIntOrNull().orNol()
                    cardRequest.add(CartRequest(productId, quantity))
                }
                else -> {}
            }
        }

        val map = cardRequest.associate {
            productRepository.getProduct(it.productId) to it.quantity
        }

        val cartData = when (method) {
            Method.ADD -> cartRepository.addProductToCart(user.id, map).mapToResponse()
            Method.REMOVE -> cartRepository.deleteProductInCart(user.id, map).mapToResponse()
        }

        val message = when (method) {
            Method.ADD -> "Add cart"
            Method.REMOVE -> "Delete cart"
        }

        respond(MainResponse.bindToResponse(cartData, message))
    }

    private suspend fun getUser(request: ApplicationRequest): User {
        return User.fromToken(request, userRepository, User.Role.CUSTOMER)
    }

    private suspend fun validateAccess(request: ApplicationRequest): User {
        val user = getUser(request)
        if (user.role == User.Role.SELLER) throw MainException("Not available for seller", HttpStatusCode.BadGateway)
        return user
    }

    private suspend fun getCartForUser(request: ApplicationRequest): Cart {
        val user = validateAccess(request)
        return cartRepository.getCart(user.id)
    }

    enum class Method {
        ADD, REMOVE
    }
}