package com.aej.repository.cart

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.repository.product.Product
import com.aej.utils.orNol
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.eq
import java.time.Instant

class CartRepositoryImpl : CartRepository {

    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Cart>("cart")

    override suspend fun createCart(cart: Cart): Boolean {
        val cartExist = collection.findOne(Cart::id eq cart.id)
        if (cartExist != null) throw MainException("Cart already exist", HttpStatusCode.Conflict)
        collection.insertOne(cart)
        return true
    }

    override suspend fun getCart(ownerId: String): Cart {
        return collection.findOne(Cart::owner eq ownerId).orThrow()
    }

    override suspend fun updateCart(cart: Cart): Cart {
        collection.updateOne(Cart::id eq cart.id, cart)
        return getCart(cart.owner)
    }

    override suspend fun addProductToCart(ownerId: String, map: Map<Product, Int>): Cart {
        return addDeleteCart(ownerId, map, AddDelete.ADD)
    }

    override suspend fun deleteProductInCart(ownerId: String, map: Map<Product, Int>): Cart {
        return addDeleteCart(ownerId, map, AddDelete.DELETE)
    }

    override suspend fun clearCart(ownerId: String): Boolean {
        val existingCart = getCart(ownerId)
        existingCart.amount = 0L
        existingCart.product = emptyList()
        collection.updateOne(Cart::owner eq ownerId, existingCart)
        return true
    }

    private suspend fun addDeleteCart(ownerId: String, map: Map<Product, Int>, addDelete: AddDelete): Cart {
        val existingCart = getCart(ownerId)
        val existingProduct = existingCart.product.map { it.product to it.quantity }

        val updatedProduct = (map + existingProduct).map {
            val productQuantity: Pair<Cart.ProductData, Int> = when (addDelete) {
                AddDelete.ADD -> {
                    val currentProduct =
                        existingCart.product.find { cart -> cart.product.id == it.key.id } ?: Cart.ProductData()
                    val addedProduct = map[it.key].orNol()
                    val updatedQuantity = currentProduct.quantity + addedProduct
                    Pair(currentProduct, updatedQuantity)
                }
                AddDelete.DELETE -> {
                    val currentProduct = existingCart.product.find { cart -> cart.product.id == it.key.id }
                        ?: throw MainException("\"${it.key.name}\" not found!", HttpStatusCode.Forbidden)
                    val addedProduct = map[it.key].orNol()
                    val updatedQuantity = currentProduct.quantity - addedProduct
                    Pair(currentProduct, updatedQuantity)
                }
            }
            Cart.ProductData(
                product = it.key,
                quantity = productQuantity.second,
                createdAt = productQuantity.first.createdAt
            )
        }

        val amount = updatedProduct.sumOf { it.product.price * it.quantity }

        existingCart.product = updatedProduct.sortedBy { Instant.parse(it.createdAt) }
        existingCart.amount = amount

        return updateCart(existingCart)
    }

    private enum class AddDelete {
        ADD, DELETE
    }
}