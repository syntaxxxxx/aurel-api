package com.aej.screen.request

data class BannerRequest(
    var name: String = "",
    var headline: String = "",
    var caption: String = "",
    var imageUrl: String = "",
    var productId: String = ""
)