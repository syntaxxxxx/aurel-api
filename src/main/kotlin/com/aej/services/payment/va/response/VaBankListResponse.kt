package com.aej.services.payment.va.response

import com.google.gson.annotations.SerializedName

data class VaBankListResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("is_activated")
    val isActivated: Boolean
)