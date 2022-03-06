package com.aej.repository.product

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.repository.category.Category
import com.aej.utils.isEmpty
import com.aej.utils.isNotEmpty
import com.aej.utils.orThrow
import io.ktor.http.*
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.util.KMongoUtil

class ProductRepositoryImpl : ProductRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Product>("product")
    private val categoryRepository = KoinContainer.categoryRepository

    override suspend fun createProduct(product: Product): Boolean {
        val productExist = collection.findOne(Product::name eq product.name)
        if (productExist != null) throw MainException("Product already exist", HttpStatusCode.Conflict)
        collection.insertOne(product)
        return true
    }

    override suspend fun getProduct(id: String): Product {
        return collection.findOne(Product::id eq id).orThrow()
    }

    override suspend fun getProductByOwner(ownerId: String): Pair<List<Product>, Long> {
        val product = collection.find(Product::owner eq ownerId).toList()
        val count = product.count().toLong()
        return Pair(product, count)
    }

    override suspend fun getAllProduct(): List<Product> {
        return collection.find().toList()
    }

    override suspend fun searchProduct(
        key: String,
        page: Int,
        limit: Int,
        ownerId: String,
        category: Category,
        sort: ProductSort
    ): Pair<List<Product>, Long> {
        val offset = (page - 1) * limit

        val product: Pair<List<Product>, Long> = when {
            key.isNotEmpty() && ownerId.isEmpty() -> {
                val filter = filterWithCategory(category, bsonOf(nameRegexFilterOf(key)))
                val product = collection.find(filter)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
                val count = collection.countDocuments(filter)

                Pair(product, count)
            }
            ownerId.isNotEmpty() && key.isNotEmpty() -> {
                val filter = filterWithCategory(category, Product::owner eq ownerId)
                val product = collection.find(filter)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()

                val count = collection.countDocuments(filter)
                Pair(product, count)
            }
            else -> {
                val product = collection.find(nameRegexFilterOf(key))
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
                val count = collection.countDocuments(nameRegexFilterOf(key))
                Pair(product, count)
            }
        }

        return product
    }

    override suspend fun getSizeCount(key: String): Long {
        val count = if (key.isNotEmpty()) {
            collection.countDocuments(Product::name regex nameRegexFilterOf(key))
        } else {
            collection.countDocuments()
        }
        return count
    }

    override suspend fun getProductPage(
        page: Int,
        limit: Int,
        ownerId: String,
        categoryId: String,
        sort: ProductSort
    ): Pair<List<Product>, Long> {
        val offset = (page - 1) * limit
        val category = categoryRepository.getCategoryOrEmptyById(categoryId)

        val product: Pair<List<Product>, Long> = when {
            ownerId.isNotEmpty() && category.isEmpty() -> {
                val filter = and(
                    Product::owner eq ownerId
                )

                val product = collection.find(filter)
                    .manageSort(sort)
                    .toList()
                val count = collection.countDocuments(filter)
                Pair(product, count)
            }
            category.isNotEmpty() && ownerId.isEmpty() -> {
                val filter = and(
                    Product::category eq category
                )

                val product = collection.find(filter)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
                val count = collection.countDocuments(filter)

                Pair(product, count)
            }
            ownerId.isNotEmpty() && category.isNotEmpty() -> {
                val filter = and(
                    Product::category eq category,
                    Product::owner eq ownerId
                )
                val product = collection.find(filter)
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()
                val count = collection.countDocuments(filter)
                Pair(product, count)
            }
            else -> {
                val product = collection.find()
                    .manageSort(sort)
                    .skip(offset)
                    .limit(limit)
                    .toList()

                val count = collection.countDocuments()
                Pair(product, count)
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

    override suspend fun getProductInCategory(categoryId: String): Pair<List<Product>, Long> {
        val category = categoryRepository.getCategoryOrEmptyById(categoryId)
        val product = collection.find(Product::category eq category).toList()
        val count = collection.countDocuments(Product::category eq category)
        return Pair(product, count)
    }

    override suspend fun fixProduct() {
        getAllProduct().forEach {
            val newProd = it
            newProd.imageUrl = newProd.imageUrl.replace("http://0.0.0.0:8081", "https://aurel-store.herokuapp.com")
            updateProduct(newProd)
        }
    }

    private fun nameRegexFilterOf(key: String): String {
        return "{ name : { '\$regex' : '$key.*', '\$options' : 'i' } }"
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
    private fun bsonSortPopular() = descending(Product::popularity)
    private fun bsonSortPrice() = ascending(Product::price)

    private fun filterWithCategory(category: Category, vararg bson: Bson): Bson {
        return if (category.id.isNotEmpty()) {
            and(Product::category eq category, *bson)
        } else {
            and(*bson)
        }
    }

    private fun bsonOf(regex: String): Bson = KMongoUtil.toBson(regex)
}