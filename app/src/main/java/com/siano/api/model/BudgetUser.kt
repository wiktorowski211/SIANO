package com.siano.api.model

data class BudgetUser(
    val id: Long,
    val name: String,
    val budgetId: Long,
    val userId: Long?
)