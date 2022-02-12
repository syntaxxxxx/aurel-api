package com.aej.screen.request

data class ProductRequest(
    var name: String = "",
    var stock: Int = 0,
    var price: Long = 0,
    var category: String = "",
    var imageUrl: String = ""
)
