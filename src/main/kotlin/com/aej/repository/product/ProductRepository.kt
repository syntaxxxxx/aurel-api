package com.aej.repository.product

interface ProductRepository {
    suspend fun createProduct(product: Product): Boolean
    suspend fun getProduct(id: String): Product
    suspend fun getProductByOwner(ownerId: String): List<Product>
    suspend fun getAllProduct(): List<Product>
    suspend fun searchProduct(key: String, page: Int, limit: Int, ownerId: String, sort: ProductSort = ProductSort.DATE): List<Product>
    suspend fun getSizeCount(key: String = ""): Long
    suspend fun getProductPage(page: Int, limit: Int, ownerId: String, categoryId: String, sort: ProductSort = ProductSort.DATE): List<Product>
    suspend fun updateProduct(product: Product): Product
    suspend fun incrementProduct(id: String, quantity: Int): Product
    suspend fun decrementProduct(id: String, quantity: Int): Product
    suspend fun deleteProduct(id: String): Boolean
    suspend fun getProductInCategory(categoryId: String): List<Product>
    suspend fun fixProduct()

    companion object {
        const val PER_PAGE = 4
    }
}