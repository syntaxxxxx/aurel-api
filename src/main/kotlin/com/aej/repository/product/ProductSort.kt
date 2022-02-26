package com.aej.repository.product

enum class ProductSort {
    DATE, POPULAR, PRICE
}

fun ProductSort.name() = name.lowercase()
fun String.toProductSort() = try {
    ProductSort.valueOf(uppercase())
} catch (e: IllegalArgumentException) {
    ProductSort.DATE
}