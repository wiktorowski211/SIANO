package com.siano.api

import com.siano.api.model.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

   // Trash

    @GET("api/budgets")
    fun authorizeUser(): Single<ResponseBody>

    @GET("api/user/repos?affiliation=owner")
    fun getUserRepositories(): Single<List<Repository>>

    // Budgets

    @GET("api/budgets")
    fun getUserBudgets(): Single<Response<List<Budget>>>

    @POST("api/budgets")
    fun createBudget(@Body request: BudgetRequest): Single<Unit>

    @DELETE("api/budgets/{budget_id}")
    fun deleteBudget(@Path("budget_id") budgetId: String): Single<ResponseBody>

    // Transactions

    @GET("api/budgets/{budget_id}/transactions")
    fun getBudgetTransactions(@Path("budget_id") budgetId: String): Single<Response<List<Transaction>>>

    @POST("api/budgets/{budget_id}/transactions")
    fun createTransaction(@Path("budget_id") budgetId: String, @Body request: TransactionRequest): Single<Unit>

    // Members

    // Users
}
