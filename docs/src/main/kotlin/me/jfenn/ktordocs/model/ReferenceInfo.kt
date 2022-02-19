package me.jfenn.ktordocs.model

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

data class ReferenceInfo(
    val url: String,
    val title: String = url
)