package com.aej.repository.category

import com.aej.container.KoinContainer
import com.aej.utils.orThrow
import org.litote.kmongo.eq

class CategoryRepositoryImpl : CategoryRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Category>("category")

    override suspend fun createCategory(category: Category): Boolean {
        collection.insertOne(category)
        return true
    }

    override suspend fun getCategory(): List<Category> {
        return collection.find().toList()
    }

    override suspend fun getCategoryById(id: String): Category {
        return collection.findOne(Category::id eq id).orThrow()
    }
}