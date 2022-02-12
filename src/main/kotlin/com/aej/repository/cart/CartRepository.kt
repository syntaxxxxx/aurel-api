package com.aej.repository.cart

import com.aej.repository.product.Product

interface CartRepository {
    suspend fun createCart(cart: Cart): Boolean
    suspend fun getCart(ownerId: String): Cart
    suspend fun updateCart(cart: Cart): Cart
    suspend fun addProductToCart(ownerId: String, map: Map<Product, Int>): Cart
    suspend fun deleteProductInCart(ownerId: String, map: Map<Product, Int>): Cart
    suspend fun clearCart(ownerId: String): Boolean
}