package com.aej.repository.product

import com.aej.MainException
import com.aej.repository.user.User
import com.aej.utils.AESUtils
import com.aej.utils.isNol
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.http.*

data class Product(
    var id: String = "",
    var name: String = "",
    var owner: String = "",
    var stock: Int = 0,
    var price: Long = 0,
    var category: String = "",
    var imageUrl: String = "",
    var userInfo: UserInfo = UserInfo()
) {
    data class UserInfo(
        var id: String = "",
        var name: String = ""
    )
    companion object {
        fun of(name: String, owner: String, stock: Int, price: Long, category: String, imageUrl: String = ""): Product {
            val hashId = AESUtils.encrypt(name).take(8)
            return Product(hashId, name, owner, stock, price, category)
        }

        fun fromStringRaw(string: String): Product {
            val type = object : TypeToken<Product>() {}.type
            return try {
                Gson().fromJson(string, type)
            } catch (e: Throwable) {
                throw MainException("Product not found", HttpStatusCode.NotFound)
            }
        }

        fun fromStringRawNullable(string: String): Product? {
            val type = object : TypeToken<Product>() {}.type
            return try {
                Gson().fromJson(string, type)
            } catch (e: Throwable) {
                null
            }
        }
    }

    fun validateQuantity(): Product {
        if (stock < 0) stock = 0
        return this
    }

    fun validateItem(): Product {
        if (id == "") id = AESUtils.encrypt(name).take(8)
        when {
            name.isEmpty() -> throwEmptyParam("name")
            stock.isNol() -> throwEmptyParam("quantity")
            price.isNol() -> throwEmptyParam("price")
            category.isEmpty() -> throwEmptyParam("category")
        }
        return this
    }

    fun withUserInfo(user: User): Product {
        userInfo = UserInfo(user.id, user.name)
        return this
    }

    private fun throwEmptyParam(param: String) {
        throw MainException("Field `$param` is required", HttpStatusCode.BadRequest)
    }
}
