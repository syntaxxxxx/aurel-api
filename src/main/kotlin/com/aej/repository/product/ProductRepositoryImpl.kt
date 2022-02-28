package com.aej.repository.product

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.utils.orThrow
import io.ktor.http.*
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineFindPublisher

class ProductRepositoryImpl : ProductRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Product>("product")

    override suspend fun createProduct(product: Product): Boolean {
        val productExist = collection.findOne(Product::id eq product.id)
        if (productExist != null) throw MainException("Product already exist", HttpStatusCode.Conflict)
        collection.insertOne(product)
        return true
    }

    override suspend fun getProduct(id: String): Product {
        return collection.findOne(Product::id eq id).orThrow()
    }

    override suspend fun getProductByOwner(ownerId: String): List<Product> {
        return collection.find(Product::owner eq ownerId).toList()
    }

    override suspend fun getAllProduct(): List<Product> {
        return collection.find().toList()
    }

    override suspend fun searchProduct(
        key: String,
        page: Int,
        limit: Int,
        ownerId: String,
        sort: ProductSort
    ): List<Product> {
        val offset = (page - 1) * limit
        val pipeline = listOf(getBsonBySort(sort), skip(offset), limit(limit))

        val product = when {
            ownerId.isNotEmpty() && key.isEmpty() -> {
                collection.find(Product::name eq ownerId)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
            }
            key.isNotEmpty() && ownerId.isEmpty() -> {
                collection.find(Product::name regex getRegexOfKey(key))
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
            }
            ownerId.isNotEmpty() && key.isNotEmpty() -> {
                collection.find()
                    .filter(Product::name regex getRegexOfKey(key))
                    .filter(Product::owner eq ownerId)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
            }
            else -> {
                collection.aggregate<Product>(pipeline).toList()
            }
        }

        return product
    }

    override suspend fun getSizeCount(key: String): Long {
        val count = if (key.isNotEmpty()) {
            collection.countDocuments(Product::name regex getRegexOfKey(key))
        } else {
            collection.countDocuments()
        }
        return count
    }

    override suspend fun getProductPage(
        page: Int,
        limit: Int,
        ownerId: String,
        category: String,
        sort: ProductSort
    ): List<Product> {
        val offset = (page - 1) * limit
        val pipeline = listOf(getBsonBySort(sort), skip(offset), limit(limit))

        val product = when {
            ownerId.isNotEmpty() && category.isEmpty() -> {
                collection.find(Product::owner eq ownerId)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
            }
            category.isNotEmpty() && ownerId.isEmpty() -> {
                collection.find(Product::category eq category)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
            }
            ownerId.isNotEmpty() && category.isNotEmpty() -> {
                collection.find()
                    .filter(Product::category eq category)
                    .filter(Product::owner eq ownerId)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
            }
            else -> {
                collection.aggregate<Product>(pipeline).toList()
            }
        }

        return product
    }

    override suspend fun updateProduct(product: Product): Product {
        collection.updateOne(Product::id eq product.id, product)
        return getProduct(product.id)
    }

    override suspend fun incrementProduct(id: String, quantity: Int): Product {
        val currentProduct = getProduct(id)
        currentProduct.stock + quantity
        return updateProduct(currentProduct.validateQuantity())
    }

    override suspend fun decrementProduct(id: String, quantity: Int): Product {
        val currentProduct = getProduct(id)
        currentProduct.stock - quantity
        return updateProduct(currentProduct.validateQuantity())
    }

    override suspend fun deleteProduct(id: String): Boolean {
        collection.deleteOne(Product::id eq id)
        return true
    }

    override suspend fun getCategory(): List<String> {
        return collection.find().toList().map { it.category }.distinct()
    }

    override suspend fun getProductInCategory(category: String): List<Product> {
        return collection.find(Product::category eq category).toList()
    }

    override suspend fun fixProduct() {
        getAllProduct().forEach {
            val newProd = it
            newProd.imageUrl = newProd.imageUrl.replace("http://0.0.0.0:8081", "https://aurel-store.herokuapp.com")
            updateProduct(newProd)
        }
    }

    private fun getRegexOfKey(key: String): Regex {
        return "$key.*".toRegex()
    }

    private fun CoroutineFindPublisher<Product>.manageSort(sort: ProductSort): CoroutineFindPublisher<Product> {
        return when (sort) {
            ProductSort.DATE -> {
                sort(bsonSortDate())
            }
            ProductSort.POPULAR -> {
                sort(bsonSortPopular())
            }
            ProductSort.PRICE -> {
                sort(bsonSortPrice())
            }
        }
    }

    private fun getBsonBySort(sort: ProductSort): Bson {
        return when (sort) {
            ProductSort.DATE -> {
                sort(bsonSortDate())
            }
            ProductSort.POPULAR -> {
                sort(bsonSortPopular())
            }
            ProductSort.PRICE -> {
                sort(bsonSortPrice())
            }
        }
    }

    private fun bsonSortDate() = ascending(Product::updatedAt)
    private fun bsonSortPopular() = descending(Product::soldPercent)
    private fun bsonSortPrice() = ascending(Product::price)

}