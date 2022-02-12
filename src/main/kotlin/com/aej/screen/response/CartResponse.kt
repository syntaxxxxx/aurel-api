package com.aej.screen.response

import com.aej.repository.product.Product
import java.time.Instant

data class CartResponse(
    var id: String = "",
    var owner: String = "",
    var amount: Long = 0L,
    var product: List<ProductCart> = emptyList()
) {
    data class ProductCart(
        val product: Product,
        val quantity: Int,
        var createdAt: String = "${Instant.now()}",
        var updatedAt: String = "${Instant.now()}"
    )
}