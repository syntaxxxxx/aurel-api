package com.aej.screen.response

import com.aej.repository.user.User
import me.hana.docs.annotation.DocFieldDescription

class UserResponse(
    var id: String = "",
    var username: String = "",
    var role: User.Role = User.Role.CUSTOMER,
    var imageUrl: String = "",
    var fullName: String = "",
    var city: String = "",
    var simpleAddress: String = ""
)