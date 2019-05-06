package com.siano.api.model

sealed class ResetPassword {
    data class Password(val password: String) : ResetPassword()
    data class Email(val email: String) : ResetPassword()
}

data class ResetPasswordRequest(val password_reset: ResetPassword)