package com.aej.screen.response

import com.aej.MainException
import io.ktor.http.*

data class MainResponse<T>(
    var status: Boolean = false,
    var code: Int = 500,
    var message: String = "Failed",
    var data: T? = null
) {
    companion object {
        fun <T>bindToResponse(data: T?, context: String, code: Int = 200): MainResponse<T> {
            return if (data != null) {
                MainResponse(
                    status = true,
                    code = code,
                    message = "$context success",
                    data = data
                )
            } else {
                throw MainException("User not found", HttpStatusCode.NotFound)
            }
        }
    }
}