package com.aej.container

class ValueContainer {
    private var host: String = "0.0.0.0"
    private var port: Int = 8081

    fun setHost(host: String) {
        this.host = host
    }

    fun setPort(port: Int) {
        this.port = port
    }

    fun getBaseUrl(): String {
        if (host == "0.0.0.0") return "http://$host:$port/"

        val isContainHttps = host.contains("http")
        return if (isContainHttps) {
            host
        } else {
            "https://$host"
        }
    }
}