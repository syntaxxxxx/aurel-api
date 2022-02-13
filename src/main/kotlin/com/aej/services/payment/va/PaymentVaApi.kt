package com.aej.services.payment.va

import com.aej.MainException
import com.aej.services.payment.PaymentServices
import com.aej.services.payment.simulation.request.va.VaSimulationPaidBody
import com.aej.services.payment.va.request.VaRequestBody
import com.aej.services.payment.va.response.VaBankListResponse
import com.aej.services.payment.va.response.VaCreateResponse
import com.aej.services.payment.simulation.response.SimulationPaidResponse
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

class PaymentVaApi {

    companion object {
        private const val URL_CREATED = "https://api.xendit.co/callback_virtual_accounts"
        private const val URL_LIST_BANK = "https://api.xendit.co/available_virtual_account_banks"

        private const val EXTERNAL_ID = "externalId"
    }

    suspend fun createVa(requestBody: VaRequestBody): VaCreateResponse {
        return PaymentServices.xenditPost(URL_CREATED, requestBody)
    }

    suspend fun getListVa(): List<VaBankListResponse> {
        val response = PaymentServices.xenditGet<HttpResponse>(URL_LIST_BANK)

        val stringList = response.body<String>()
        val typeToken = object : TypeToken<List<VaBankListResponse>>() {
        }.type
        val data = try {
            Gson().fromJson<List<VaBankListResponse>>(stringList, typeToken)
        } catch (e: JsonParseException) {
            e.printStackTrace()
            throw MainException(e.localizedMessage, HttpStatusCode.BadRequest)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw MainException(e.localizedMessage, HttpStatusCode.BadRequest)
        }

        return data
    }
}