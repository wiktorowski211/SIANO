package com.siano.api.model

data class Transaction(
    val budgetId:Long,
    val title: String,
    val category: String,
    val shares: List<TransactionShare>
)