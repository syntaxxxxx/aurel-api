package com.aej.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

fun Map<String, Any>.toJsonString(): String {
    val gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

    val type = object : TypeToken<Map<String, Any>>() {}.type
    return if (isNotEmpty()) {
        gson.toJson(this, type)
    } else {
        throw Throwable("Not found")
    }
}