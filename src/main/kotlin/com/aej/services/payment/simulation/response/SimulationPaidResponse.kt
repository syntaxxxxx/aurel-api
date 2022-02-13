package com.aej.services.payment.simulation.response

import com.google.gson.annotations.SerializedName

data class SimulationPaidResponse(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = ""
)