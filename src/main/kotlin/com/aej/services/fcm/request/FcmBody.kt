package com.aej.services.fcm.request

data class FcmBody(
    val to: String = "",
    val data: Any
)
