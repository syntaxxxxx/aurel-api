package com.aej.services.fcm.response

data class FcmResponse(
    val success: Int = 0,
    val failure: Int = 0,
    val results: List<FcmResult> = emptyList()
) {
    data class FcmResult(
        val messageId: String = "",
        val error: String = ""
    )
}