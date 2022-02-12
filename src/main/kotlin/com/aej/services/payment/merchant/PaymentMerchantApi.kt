package com.aej.services.payment.merchant

import com.aej.services.payment.PaymentServices
import com.aej.services.payment.merchant.request.MerchantRequestBody
import com.aej.services.payment.merchant.response.MerchantCreateResponse

class PaymentMerchantApi {

    companion object {
        private const val URL_CREATED = "https://api.xendit.co/fixed_payment_code"
    }

    suspend fun createMerchant(requestBody: MerchantRequestBody): MerchantCreateResponse {
        return PaymentServices.xenditPost(URL_CREATED, requestBody)
    }

    fun getListMerchant() = listOf("Alfamart", "Indomaret")

}