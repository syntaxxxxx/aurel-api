package com.aej.services.payment.simulation.request.merchant

data class MerchantSimulationPaidBody(
    var paymentCode: String = "",
    var retailOutletName: String = "",
    var transferAmount: Long = 0
)