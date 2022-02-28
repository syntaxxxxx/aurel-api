package com.aej.repository.banner

interface BannerRepository {
    suspend fun createBanner(banner: Banner): Boolean
    suspend fun getBanner(): List<Banner>
    suspend fun getBannerId(id: String): Banner
}