package com.aej.screen.response

data class ProductResponse(
    var id: String = "",
    var name: String = "",
    var seller: UserInfo = UserInfo(),
    var stock: Int = 0,
    var category: String = "",
    var price: Long = 0,
    var imageUrl: String = "",
    var description: String = ""
) {
    data class UserInfo(
        var id: String = "",
        var name: String = ""
    )
}