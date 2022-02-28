package com.aej.repository.banner

import com.aej.MainException
import com.aej.container.KoinContainer
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.eq

class BannerRepositoryImpl : BannerRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Banner>("banner")

    override suspend fun createBanner(banner: Banner): Boolean {
        val bannerExist = collection.findOne(Banner::name eq banner.name)
        if (bannerExist != null) throw MainException("Banner already exist", HttpStatusCode.Conflict)
        collection.insertOne(banner)
        return true
    }

    override suspend fun getBanner(): List<Banner> {
        return collection.find().toList()
    }

    override suspend fun getBannerId(id: String): Banner {
        return collection.findOne(Banner::id eq id).orThrow()
    }
}