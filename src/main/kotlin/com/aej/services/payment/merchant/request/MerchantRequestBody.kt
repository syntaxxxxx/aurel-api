package com.aej.services.payment.merchant.request


import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.utils.removeMerchantSuffix
import com.google.gson.annotations.SerializedName

data class MerchantRequestBody(
    @SerializedName("expected_amount")
    var expectedAmount: Long = 0,
    @SerializedName("external_id")
    var externalId: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("retail_outlet_name")
    var retailOutletName: String = ""
) {
    companion object {
        fun of(user: User, transaction: Transaction): MerchantRequestBody {
            val externalId = "payment-${user.username}-${transaction.id}"
            return MerchantRequestBody(
                expectedAmount = transaction.amount,
                externalId = externalId,
                name = user.username,
                retailOutletName = transaction.paymentTransaction.method.removeMerchantSuffix()
            )
        }
    }
}