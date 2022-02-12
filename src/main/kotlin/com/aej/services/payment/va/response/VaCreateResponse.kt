package com.aej.services.payment.va.response

data class VaCreateResponse(
    var accountNumber: String = "",
    var bankCode: String = "",
    var currency: String = "",
    var expirationDate: String = "",
    var externalId: String = "",
    var id: String = "",
    var isClosed: Boolean = false,
    var isSingleUse: Boolean = false,
    var merchantCode: String = "",
    var name: String = "",
    var ownerId: String = "",
    var status: String = ""
)