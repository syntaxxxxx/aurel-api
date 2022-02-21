package com.aej.services.payment.va.request

import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.utils.removeVASuffix
import com.google.gson.annotations.SerializedName

data class VaRequestBody(
    @SerializedName("amount")
    var amount: Long = 0,
    @SerializedName("expected_amount")
    var expectedAmount: Long = 0,
    @SerializedName("bank_code")
    var bank_code: String = "",
    @SerializedName("external_id")
    var external_id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("virtual_account_number")
    var virtual_account_number: String = "",
    @SerializedName("is_single_use")
    var isSingleUse: Boolean = true,
    @SerializedName("is_closed")
    var isClosed: Boolean = true
) {
    companion object {
        fun of(user: User, transaction: Transaction): VaRequestBody {
            val externalId = "payment-${user.username}-${transaction.id}"
            val randomVANumber = (9999000001..9999999999).random().toString()
            return VaRequestBody(
                amount = transaction.amount,
                expectedAmount = transaction.amount,
                bank_code = transaction.paymentTransaction.method.removeVASuffix(),
                external_id = externalId,
                name = user.username,
                virtual_account_number = randomVANumber,
                isSingleUse = true,
                isClosed = true
            )
        }
    }
}