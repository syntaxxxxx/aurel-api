package com.aej.services.payment

import com.aej.container.KoinContainer
import com.aej.services.payment.merchant.PaymentMerchantApi
import com.aej.services.payment.merchant.request.MerchantRequestBody
import com.aej.services.payment.va.PaymentVaApi
import com.aej.services.payment.va.request.VaRequestBody
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object PaymentServices {

    val XENDIT_API_KEY: String = System.getenv("XENDIT_API")

    suspend inline fun <reified T, reified U>xenditPost(url: String, body: U): T {
        val client = KoinContainer.httpClient
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", XENDIT_API_KEY)
            setBody(body)
        }
        return response.body()
    }

    suspend inline fun <reified T>xenditGet(url: String): T {
        val client = KoinContainer.httpClient
        val response = client.get(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", XENDIT_API_KEY)
        }
        return response.body()
    }

    suspend fun getAllPaymentMethod() = PaymentAvailableData.createAll()

    object VirtualAccount {
        private val vaApi = PaymentVaApi()
        suspend fun getListVa() = vaApi.getListVa()
        suspend fun createVa(requestBody: VaRequestBody) = vaApi.createVa(requestBody)
    }

    object Merchant {
        private val merchantApi = PaymentMerchantApi()
        suspend fun getListMerchant() = merchantApi.getListMerchant()
        suspend fun createMerchant(requestBody: MerchantRequestBody) = merchantApi.createMerchant(requestBody)
    }
}