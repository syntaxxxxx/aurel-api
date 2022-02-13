package com.aej.screen.response

import com.aej.repository.payment.Payment

data class PaymentResponse(
    var id: String = "",
    var ownerId: String = "",
    var amount: Long = 0,
    var status: String = "",
    var type: String = "",
    var method: String = "",
    var referenceId: String = "",
    var transactionId: String = "",
    var externalData: Payment.ExternalData? = null,
    var expirationDate: String = ""
)