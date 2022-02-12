package com.aej.services.payment.merchant.response


import com.google.gson.annotations.SerializedName

data class MerchantCreateResponse(
    @SerializedName("expected_amount")
    var expectedAmount: Int = 0,
    @SerializedName("expiration_date")
    var expirationDate: String = "",
    @SerializedName("external_id")
    var externalId: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("is_single_use")
    var isSingleUse: Boolean = false,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("owner_id")
    var ownerId: String = "",
    @SerializedName("payment_code")
    var paymentCode: String = "",
    @SerializedName("prefix")
    var prefix: String = "",
    @SerializedName("retail_outlet_name")
    var retailOutletName: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("type")
    var type: String = ""
)