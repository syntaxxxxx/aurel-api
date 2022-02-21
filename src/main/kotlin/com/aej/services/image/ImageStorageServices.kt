package com.aej.services.image

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.utils.isImagesType
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.eq
import java.util.*

object ImageStorageServices {

    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<ImageFileData>("images")
    private val tika = KoinContainer.tika
    private val mimeTypes = KoinContainer.mimeTypes

    suspend fun uploadFile(byteArray: ByteArray, originalFileName: String, owner: String, host: String): String {
        val mimeType = tika.detect(originalFileName)
        val extensionsTika = mimeTypes.forName(mimeType)
        val extensions = extensionsTika.extension

        val randomString = UUID.randomUUID().toString()
        val fileName = "${owner}_${randomString}"

        val contentType = ContentType.parse(mimeType)
        if (!contentType.isImagesType()) throw MainException("Image file invalid!", HttpStatusCode.BadRequest)

        val data = ImageFileData(
            name = "$fileName$extensions",
            contentType = mimeType,
            byteArray = byteArray
        )

        collection.insertOne(data)
        return "$host/image/$fileName$extensions"
    }

    suspend fun getFile(imageName: String): ImageFileData {
        return collection.findOne(ImageFileData::name eq imageName).orThrow()
    }
}