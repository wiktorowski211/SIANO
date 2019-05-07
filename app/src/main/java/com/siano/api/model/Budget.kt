package com.siano.api.model

data class Budget(val id: Long, val name: String, val invite_code: String, val color: String?, val owner_id: Long)

data class BudgetRequest(val budget: Budget)
