package me.jfenn.ktordocs

import io.ktor.http.HttpMethod
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import me.jfenn.ktordocs.model.AuthenticationInfo
import me.jfenn.ktordocs.model.Configuration
import me.jfenn.ktordocs.model.EndpointInfo
import me.jfenn.ktordocs.model.ParameterInfo

class RestDocs(
    configure: Configuration.() -> Unit
) {

    val config = Configuration().apply { configure() }
    val htmlBuilder = HtmlBuilder(this)

    val endpoints = ArrayList<EndpointInfo>()

    fun endpoint(info: EndpointInfo) {
        endpoints.add(info)
    }

}