package com.aej.screen.routing

import com.aej.container.KoinContainer
import com.aej.repository.category.Category
import com.aej.screen.response.MainResponse
import com.aej.services.image.ImageStorageServices
import com.aej.utils.mapToResponse
import com.aej.utils.orRandom
import com.aej.utils.randomString
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object CategoryRouteScreen {
    private val valueContainer = KoinContainer.valueContainer
    private val categoryRepository = KoinContainer.categoryRepository

    suspend fun getAllCategory(applicationCall: ApplicationCall) = applicationCall.run {
        val categories = categoryRepository.getCategory().map { it.mapToResponse() }
        respond(MainResponse.bindToResponse(categories, "Get available category"))
    }

    suspend fun createCategory(applicationCall: ApplicationCall) = applicationCall.run {
        val category = Category()
        receiveMultipart().readAllParts().onEach { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> category.name = part.value
                    }
                }
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    val baseUrl = valueContainer.getBaseUrl()
                    val urlImage = ImageStorageServices.uploadFile(fileBytes, part.originalFileName.orRandom(), randomString(), baseUrl)
                    when (part.name) {
                        "image_cover" -> category.imageCover = urlImage
                        "image_icon" -> category.imageIcon = urlImage
                    }
                }
                else -> {}
            }
        }

        categoryRepository.createCategory(category)
        val categories = categoryRepository.getCategory().map { it.mapToResponse() }
        respond(MainResponse.bindToResponse(categories, "Get available category"))

    }
}