package com.aej.repository.product

import com.aej.MainException
import com.aej.repository.user.User
import com.aej.utils.isNol
import com.aej.utils.randomString
import io.ktor.http.*
import java.time.Instant

data class Product(
    var id: String = "",
    var name: String = "",
    var owner: String = "",
    var stock: Int = 0,
    var price: Long = 0,
    var category: String = "",
    var imageUrl: String = "",
    var description: String = "",
    var userInfo: UserInfo = UserInfo(),
    var soldCount: Long = 0L,
    var createdAt: String = "${Instant.now()}",
    var updatedAt: String = "${Instant.now()}"
) {
    data class UserInfo(
        var id: String = "",
        var name: String = "",
        var city: String = ""
    )
    companion object {
        fun of(name: String, owner: String, stock: Int, price: Long, category: String): Product {
            val hashId = randomString()
            return Product(hashId, name, owner, stock, price, category)
        }
    }

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
            category.isEmpty() -> throwEmptyParam("category")
            description.isEmpty() -> throwEmptyParam("description")
        }
        return this
    }

    fun withUserInfo(user: User): Product {
        userInfo = UserInfo(user.id, user.username)
        return this
    }

    private fun throwEmptyParam(param: String) {
        throw MainException("Field `$param` is required", HttpStatusCode.BadRequest)
    }
}
