package com.aej.screen.routing

import com.aej.MainException
import com.aej.container.KoinContainer
import com.aej.repository.banner.Banner
import com.aej.repository.user.User
import com.aej.screen.request.BannerRequest
import com.aej.screen.response.MainResponse
import com.aej.services.image.ImageStorageServices
import com.aej.utils.mapToResponse
import com.aej.utils.orNol
import com.aej.utils.orRandom
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object BannerRouteScreen {
    private val userRepository = KoinContainer.userRepository
    private val bannerRepository = KoinContainer.bannerRepository
    private val valueContainer = KoinContainer.valueContainer

    suspend fun createBanner(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository, User.Role.SELLER)
        val banner = Banner(sellerId = user.id)

        receiveMultipart().readAllParts().onEach { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> banner.name = part.value
                        "headline" -> banner.headline = part.value
                        "caption" -> banner.caption = part.value
                        "product_id" -> banner.productId = part.value
                    }
                }
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    val baseUrl = valueContainer.getBaseUrl()

                    val urlImage = ImageStorageServices.uploadFile(fileBytes, part.originalFileName.orRandom(), user.username, baseUrl)
                    banner.imageUrl = urlImage
                }
                else -> {}
            }
        }

        bannerRepository.createBanner(banner)
        val bannerDb = bannerRepository.getBannerId(banner.id)
        val bannerResponse = bannerDb.mapToResponse()
        respond(MainResponse.bindToResponse(bannerResponse, "Create banner"))
    }

    suspend fun getAllBanner(applicationCall: ApplicationCall) = applicationCall.run {
        val bannerDb = bannerRepository.getBanner()
        val bannerResponse = bannerDb.map { it.mapToResponse() }
        respond(MainResponse.bindToResponse(bannerResponse, "Get banner"))
    }
}