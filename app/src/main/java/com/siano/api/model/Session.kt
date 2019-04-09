package com.siano.api.model

sealed class Session {
    data class Username(val username: String, val password: String) : Session()
    data class Email(val email: String, val password: String) : Session()
}

data class SessionRequest(val session: Session)
