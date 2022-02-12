package com.aej.services.fcm

import com.aej.KoinContainer
import com.aej.repository.user.User
import com.aej.services.fcm.request.FcmBody
import com.aej.services.fcm.request.FcmData
import com.aej.services.fcm.response.FcmResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object FcmServices {
    private val SERVER_KEY = System.getenv("FCM_SERVER_KEY")
    private const val BASE_URL = "https://fcm.googleapis.com/fcm/send"

    private val client = KoinContainer.httpClient

    suspend fun createNotification(user: User, fcmData: FcmData): FcmResponse {
        println("fcm notification - create notif....")
        val response = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "key=$SERVER_KEY")
            val body = FcmBody(
                to = user.fcmToken,
                data = fcmData
            )

            setBody(body)
        }

        return response.body()
    }

}