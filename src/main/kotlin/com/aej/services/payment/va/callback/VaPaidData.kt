package com.aej.services.payment.va.callback


import com.google.gson.annotations.SerializedName

data class VaPaidData(
    @SerializedName("account_number")
    var accountNumber: String = "",
    @SerializedName("amount")
    var amount: Int = 0,
    @SerializedName("bank_code")
    var bankCode: String = "",
    @SerializedName("callback_virtual_account_id")
    var callbackVirtualAccountId: String = "",
    @SerializedName("created")
    var created: String = "",
    @SerializedName("external_id")
    var externalId: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("merchant_code")
    var merchantCode: String = "",
    @SerializedName("owner_id")
    var ownerId: String = "",
    @SerializedName("payment_id")
    var paymentId: String = "",
    @SerializedName("transaction_timestamp")
    var transactionTimestamp: String = "",
    @SerializedName("updated")
    var updated: String = ""
)