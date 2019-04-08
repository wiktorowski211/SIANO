package com.siano.api.model

data class Member(
    val id: Long,
    val nickname: String,
    val budget_id: Long,
    val user_id: Long?
)

data class MemberRequest(val member: Member)