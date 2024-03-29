package com.aej.repository.product

import com.aej.MainException
import com.aej.repository.category.Category
import com.aej.repository.user.User
import com.aej.utils.isEmpty
import com.aej.utils.isNol
import com.aej.utils.randomString
import io.ktor.http.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Instant
import kotlin.math.roundToInt

data class Product(
    var id: String = "",
    var name: String = "",
    var owner: String = "",
    var stock: Int = 0,
    var price: Long = 0,
    var category: Category = Category(),
    var imageUrl: String = "",
    var description: String = "",
    var userInfo: UserInfo = UserInfo(),
    var soldCount: Long = 0L,
    var popularity: Double = 0.0,
    var createdAt: String = "${Instant.now()}",
    var updatedAt: String = "${Instant.now()}"
) {
    data class UserInfo(
        var id: String = "",
        var name: String = "",
        var city: String = ""
    )

    fun validateQuantity(): Product {
        if (stock < 0) stock = 0
        return this
    }

    fun validateItem(): Product {
        if (id == "") id = randomString()

        when {
            name.isEmpty() -> throwEmptyParam("name")
            stock.isNol() -> throwEmptyParam("stock")
            price.isNol() -> throwEmptyParam("price")
            description.isEmpty() -> throwEmptyParam("description")
            category.isEmpty() -> throwEmptyParam("category")
        }
        return this
    }

    fun withUserInfo(user: User): Product {
        userInfo = UserInfo(user.id, user.username, user.city)
        return this
    }

    fun calculateSoldPercent(): Product {
        val decimalFormat = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.CEILING
        }
        val percent = (soldCount.toDouble() / stock.toDouble()) * 100
        popularity = decimalFormat.format(percent).toDouble()
        return this
    }

    private fun throwEmptyParam(param: String) {
        throw MainException("Field `$param` is required", HttpStatusCode.BadRequest)
    }
}
