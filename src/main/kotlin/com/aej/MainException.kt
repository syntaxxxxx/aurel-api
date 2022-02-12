package com.aej

import io.ktor.http.*

class MainException(
    message: String = "",
    val code: HttpStatusCode = HttpStatusCode.InternalServerError
) : Throwable(message)