package com.aej.screen.routing

import com.aej.MainException
import com.aej.container.KoinContainer
import com.aej.repository.banner.Banner
import com.aej.repository.user.User
import com.aej.screen.request.BannerRequest
import com.aej.screen.response.MainResponse
import com.aej.utils.mapToResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object BannerRouteScreen {
    private val userRepository = KoinContainer.userRepository
    private val bannerRepository = KoinContainer.bannerRepository

    suspend fun createBanner(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val request = receive<BannerRequest>()

        if (user.role == User.Role.CUSTOMER) throw MainException("Role not permission", HttpStatusCode.BadRequest)
        if (request == BannerRequest()) throw MainException("Banner is empty", HttpStatusCode.BadRequest)

        val banner = Banner.of(request, user.id)
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