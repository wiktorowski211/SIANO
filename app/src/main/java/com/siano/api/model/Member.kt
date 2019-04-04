package com.siano.api.model

data class Member(
    val id: Long,
    val name: String,
    val budget_id: Long,
    val user_id: Long?
)