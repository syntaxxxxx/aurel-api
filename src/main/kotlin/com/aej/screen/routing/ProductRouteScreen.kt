package com.aej.screen.routing

import com.aej.container.KoinContainer
import com.aej.repository.product.Product
import com.aej.repository.product.ProductRepository
import com.aej.repository.product.toProductSort
import com.aej.repository.user.User
import com.aej.screen.response.MainResponse
import com.aej.screen.routing.data.PagingData
import com.aej.services.image.ImageStorageServices
import com.aej.utils.mapToResponse
import com.aej.utils.orNol
import com.aej.utils.orRandom
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object ProductRouteScreen {
    private val valueContainer = KoinContainer.valueContainer
    private val userRepository = KoinContainer.userRepository
    private val productRepository = KoinContainer.productRepository

    suspend fun createProductFormData(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository, User.Role.SELLER)
        val product = Product(owner = user.id).withUserInfo(user)
        receiveMultipart().readAllParts().onEach { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> product.name = part.value
                        "stock" -> product.stock = part.value.toIntOrNull().orNol()
                        "price" -> product.price = part.value.toLongOrNull().orNol()
                        "category" -> product.category = part.value
                        "description" -> product.description = part.value
                        "sold_count" -> product.soldCount = part.value.toLongOrNull().orNol()
                    }
                }
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    val baseUrl = valueContainer.getBaseUrl()

                    val urlImage = ImageStorageServices.uploadFile(fileBytes, part.originalFileName.orRandom(), user.username, baseUrl)
                    product.imageUrl = urlImage
                }
                else -> {}
            }
        }

        productRepository.createProduct(product.validateQuantity().validateItem())
        val productData = productRepository.getProduct(product.id).mapToResponse()
        respond(MainResponse.bindToResponse(productData, "Add product"))
    }

    suspend fun getProductWithParameter(applicationCall: ApplicationCall) = applicationCall.run {
        when {

            parameters.contains("product_id") -> getSingleProduct(this)
            parameters.contains("key") -> searchProduct(this)
            else -> getAllProduct(this)
        }
    }

    private suspend fun getSingleProduct(applicationCall: ApplicationCall) = applicationCall.run {
        val productId = parameters["product_id"].orEmpty()
        val product = productRepository.getProduct(productId).mapToResponse()
        respond(MainResponse.bindToResponse(product, "Get product"))
    }

    private suspend fun getAllProduct(applicationCall: ApplicationCall) = applicationCall.run {
        val page = parameters["page"]?.toIntOrNull() ?: 1
        val limit = parameters["per_page"]?.toIntOrNull() ?: ProductRepository.PER_PAGE
        val sellerId = parameters["seller_id"].orEmpty().replace(" ", "+")
        val category = parameters["category"].orEmpty()
        val sort = parameters["sort"].orEmpty().toProductSort()

        val products = productRepository.getProductPage(page, limit, sellerId, category, sort).map { it.mapToResponse() }
        val productsCount = productRepository.getSizeCount()
        val pagingData = PagingData(
            count = productsCount,
            countPerPage = limit.toLong(),
            currentPage = page.toLong(),
            data = products
        )
        respond(MainResponse.bindToResponse(pagingData, "Get product"))
    }

    private suspend fun searchProduct(applicationCall: ApplicationCall) = applicationCall.run {
        val page = parameters["page"]?.toIntOrNull() ?: 1
        val limit = parameters["per_page"]?.toIntOrNull() ?: ProductRepository.PER_PAGE
        val sellerId = parameters["seller_id"].orEmpty().replace(" ", "+")
        val key = parameters["key"].orEmpty()
        val sort = parameters["sort"].orEmpty().toProductSort()

        val products = productRepository.searchProduct(key, page, limit, sellerId, sort).map { it.mapToResponse() }
        val productsCount = productRepository.getSizeCount(key)
        val pagingData = PagingData(
            count = productsCount,
            countPerPage = limit.toLong(),
            currentPage = page.toLong(),
            data = products
        )
        respond(MainResponse.bindToResponse(pagingData, "Get product"))
    }

    suspend fun getAllCategory(applicationCall: ApplicationCall) = applicationCall.run {
        val categories = productRepository.getCategory()
        respond(MainResponse.bindToResponse(categories, "Get available category"))
    }
}