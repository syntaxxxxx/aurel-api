package com.aej.services.payment.va.callback


import com.google.gson.annotations.SerializedName

data class VaCreatedData(
    @SerializedName("account_number")
    var accountNumber: String = "",
    @SerializedName("bank_code")
    var bankCode: String = "",
    @SerializedName("created")
    var created: String = "",
    @SerializedName("expiration_date")
    var expirationDate: String = "",
    @SerializedName("external_id")
    var externalId: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("is_closed")
    var isClosed: Boolean = false,
    @SerializedName("is_single_use")
    var isSingleUse: Boolean = false,
    @SerializedName("merchant_code")
    var merchantCode: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("owner_id")
    var ownerId: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("updated")
    var updated: String = ""
)