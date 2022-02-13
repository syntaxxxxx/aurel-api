package com.aej.services.payment.simulation.request

data class SimulationPaidBody(
    var referenceId: String = "",
    var amount: Long = 0
)
