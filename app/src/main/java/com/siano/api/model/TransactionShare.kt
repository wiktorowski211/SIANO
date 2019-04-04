package com.siano.api.model

data class TransactionShare(
    val amount: Double,
    val member_id: Long
)

data class TransactionShareRequest(val share: TransactionShare)