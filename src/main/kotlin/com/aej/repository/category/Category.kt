package com.aej.repository.category

import com.aej.utils.randomString

data class Category(
    var id: String = "",
    var name: String = "",
    var imageCover: String = "",
    var imageIcon: String = ""
) {
    companion object {
        fun of(name: String, imageCover: String, imageIcon: String): Category {
            val id = randomString()
            return Category(id, name, imageCover, imageIcon)
        }
    }
}