package com.aej.repository.category

interface CategoryRepository {
    suspend fun createCategory(category: Category): Boolean
    suspend fun getCategory(): List<Category>
    suspend fun getCategoryById(id: String): Category
    suspend fun getCategoryOrEmptyById(id: String): Category
}