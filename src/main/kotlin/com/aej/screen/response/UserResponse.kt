package com.aej.screen.response

import com.aej.repository.user.User

class UserResponse(
    var id: String = "",
    var name: String = "",
    var role: User.Role = User.Role.CUSTOMER
)