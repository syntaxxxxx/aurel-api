package me.jfenn.ktordocs.model

import io.ktor.http.HttpMethod

class Configuration {

    var baseUrl = "http://localhost:8080"

    var title = "REST API"
    var desc = "This documentation describes the website's API endpoints."

    internal val authMethods = HashMap<String, AuthenticationInfo>()

    fun authMethod(name: String, configure: AuthenticationInfo.() -> Unit) {
        authMethods[name] = (authMethods[name] ?: AuthenticationInfo(name)).apply { configure() }
    }

    internal var defaultEndpoint = EndpointInfo("/", HttpMethod.Get)

    fun defaultProperties(configure: EndpointInfo.() -> Unit) {
        defaultEndpoint.configure()
    }

}