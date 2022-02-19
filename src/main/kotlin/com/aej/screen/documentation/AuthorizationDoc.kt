package com.aej.screen.documentation

import me.hana.docs.data.isRequired
import me.hana.docs.endpoint.EndPoint
import me.hana.docs.endpoint.headerParameter

fun EndPoint.authRequired() {
    headerParameter("Authorization", String::class) {
        description = "Basic authentication token"
        sample = "Basic qwertyuiop123456asdfgh890"
        isRequired()
    }
}