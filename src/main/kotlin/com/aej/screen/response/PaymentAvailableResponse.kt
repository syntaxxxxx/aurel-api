package com.aej.screen.response

data class PaymentAvailableResponse(
    var name: String = "",
    var code: String = "",
    var paymentType: String = "",
    var isActivated: Boolean = true
)
