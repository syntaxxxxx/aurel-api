package com.aej.services.payment.merchant.callback


import com.google.gson.annotations.SerializedName

data class MerchantPaidData(
    @SerializedName("amount")
    var amount: Int = 0,
    @SerializedName("external_id")
    var externalId: String = "",
    @SerializedName("fixed_payment_code_id")
    var fixedPaymentCodeId: String = "",
    @SerializedName("fixed_payment_code_payment_id")
    var fixedPaymentCodePaymentId: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("owner_id")
    var ownerId: String = "",
    @SerializedName("payment_code")
    var paymentCode: String = "",
    @SerializedName("payment_id")
    var paymentId: String = "",
    @SerializedName("prefix")
    var prefix: String = "",
    @SerializedName("retail_outlet_name")
    var retailOutletName: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("transaction_timestamp")
    var transactionTimestamp: String = ""
)