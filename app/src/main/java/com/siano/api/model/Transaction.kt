package com.siano.api.model

data class Transaction(
    val budget_id: Long,
    val title: String,
    val category_id: String?,
    val shares: List<TransactionShare>
)

data class TransactionRequest(val transaction: Transaction)
