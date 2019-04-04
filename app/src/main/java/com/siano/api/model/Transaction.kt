package com.siano.api.model

data class Transaction(
    val budget_id: Long,
    val title: String,
    val category: String,
    val shares: List<TransactionShare>?
)

data class TransactionRequest(val transaction: TransactionWrapper)
data class TransactionWrapper(
    val budget_id: Long,
    val title: String,
    val category: String,
    val shares: List<TransactionShareRequest>
)
