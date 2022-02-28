package com.aej.repository.banner

import com.aej.screen.request.BannerRequest
import com.aej.utils.randomString

data class Banner(
    var id: String = "",
    var name: String = "",
    var headline: String = "",
    var caption: String = "",
    var imageUrl: String = "",
    var sellerId: String = "",
    var productId: String = ""
) {
    companion object {
        fun of(request: BannerRequest, userId: String): Banner {
            val bannerId = randomString()
            return Banner(
                id = bannerId,
                name = request.name,
                headline = request.headline,
                caption = request.caption,
                imageUrl = request.imageUrl,
                sellerId = userId,
                productId = request.productId
            )
        }
    }
}