package com.aej.repository.cart

import com.aej.repository.product.Product
import com.aej.repository.user.User
import com.aej.utils.AESUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant

data class Cart(
    var id: String = "",
    var owner: String = "",
    var product: List<ProductData> = emptyList(),
    var amount: Long = 0,
    var updated: String = "${Instant.now()}"
) {
    data class ProductData(
        val product: Product = Product(),
        val quantity: Int = 0,
        var createdAt: String = "${Instant.now()}",
        var updatedAt: String = "${Instant.now()}"
    )

    companion object {
        fun of(owner: User): Cart {
            val name = "cart-${owner.name}"
            val hashId = AESUtils.encrypt(name).take(8)

            return Cart(
                id = hashId,
                owner = owner.id,
                product = emptyList()
            )
        }
    }
}
