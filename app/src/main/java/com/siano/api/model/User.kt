package com.siano.api.model

data class User(val username: String, val password: String, val email: String)

data class UserRequest(val user: User)
