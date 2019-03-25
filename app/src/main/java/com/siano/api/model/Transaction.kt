package com.siano.api.model

data class Transaction(
    val amount: Double,
    val title: String,
    val category: String,
    val shares: List<TransactionShare>
)