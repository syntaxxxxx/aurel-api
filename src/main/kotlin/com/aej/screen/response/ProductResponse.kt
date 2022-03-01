package com.aej.screen.response

data class ProductResponse(
    var id: String = "",
    var name: String = "",
    var seller: UserInfo = UserInfo(),
    var stock: Int = 0,
    var category: CategoryResponse = CategoryResponse(),
    var price: Long = 0,
    var imageUrl: String = "",
    var description: String = "",
    var soldCount: Long = 0L,
    var popularity: Double = 0.0
) {
    data class UserInfo(
        var id: String = "",
        var name: String = "",
        var city: String = ""
    )
}