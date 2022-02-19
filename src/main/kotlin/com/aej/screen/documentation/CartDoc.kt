package com.aej.screen.documentation

import com.aej.repository.product.Product
import java.time.Instant

data class CartDoc(
    var status: Boolean = true,
    var code: Int = 200,
    var message: String = "Success",
    var data: Data = Data()
) {
    data class Data(
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
}