package com.aej.screen.documentation

import me.hana.docs.annotation.DocFieldDescription
import me.hana.docs.data.isRequired
import me.hana.docs.endpoint.*

class User(
    @DocFieldDescription("status response")
    var status: Boolean = true,
    @DocFieldDescription("code response")
    var code: Int = 200,
    @DocFieldDescription("message response")
    var message: String = "Success",
    @DocFieldDescription("data response")
    var data: UserData = UserData()
)

data class UserData(
    @DocFieldDescription("id of user")
    var id: String = "122334",
    @DocFieldDescription("name of user")
    var name: String = "ucup",
    @DocFieldDescription("role of user, SELLER or CUSTOMER")
    var role: String = ""
)

class UserRequest(
    @DocFieldDescription("name of user")
    var name: String = "",
    @DocFieldDescription("password of user")
    var password: String = ""
)

class UserLogin(
    @DocFieldDescription("status response")
    var status: Boolean = true,
    @DocFieldDescription("code response")
    var code: Int = 200,
    @DocFieldDescription("message response")
    var message: String = "Success",
    @DocFieldDescription("data response")
    var data: TokenData = TokenData()
)

data class TokenData(
    @DocFieldDescription("token result")
    var token: String = "qwertyuiop12345"
)

typealias DocumentationUser = User
typealias DocumentationUserLogin = UserLogin
typealias DocumentationUserRequest = UserRequest

fun EndPoint.userParent() {
    description = """
                Documentation for user section
            """.trimIndent()

    authRequired()
}

fun EndPoint.userRegister(role: com.aej.repository.user.User.Role) {
    val desc = when (role) {
        com.aej.repository.user.User.Role.CUSTOMER -> """
                Register customer user
            """.trimIndent()
        com.aej.repository.user.User.Role.SELLER -> """
                Register seller user
            """.trimIndent()
    }

    description = desc
    bodyParameter(DocumentationUserRequest::class) {
        isRequired()
        description = "Body json"
        sample = DocumentationUserRequest(
            name = "maharani",
            password = "utsman-gantenk"
        )
    }

    responseSample(DocumentationUser().apply { data = UserData(role = role.name) })
}

fun EndPoint.userLogin() {
    description = """
                Login for generate basic authentication token, see body parameter
            """.trimIndent()

    bodyParameter(DocumentationUserRequest::class) {
        isRequired()
        description = "Body json"
        sample = DocumentationUserRequest(
            name = "maharani",
            password = "utsman-gantenk"
        )
    }

    responseSample(DocumentationUserLogin())
}

fun EndPoint.user() {
    description = """
                Get current user
            """.trimIndent()

    queryParameter("id", String::class) {
        description = "Get specified user by id"
        sample = "qwert123"
    }

    authRequired()
    responseSample(DocumentationUser())
}