package com.siano.api

import com.siano.api.model.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    // Auth

    @POST("api/sessions")
    fun authorizeUser(@Body request: SessionRequest): Single<AccessToken>

    @POST("api/users")
    fun registerUser(@Body request: UserRequest): Single<ResponseBody>

    @POST("api/password_resets")
    fun forgotPassword(@Body request: ResetPasswordRequest): Single<ResponseBody>

    @PUT("api/password_resets/update")
    fun resetPassword(@Body request: ResetPasswordRequest, @Query("key") key: String): Single<ResponseBody>

    @GET("/api/me")
    fun getUser(): Single<Response<User>>

    // Budgets

    @GET("api/budgets")
    fun getUserBudgets(): Single<Response<List<Budget>>>

    @POST("api/budgets")
    fun createBudget(@Body request: BudgetRequest): Single<Unit>

    @PUT("api/budgets/{budget_id}")
    fun editBudget(@Path("budget_id") budgetId: String, @Body request: BudgetRequest): Single<Unit>

    @DELETE("api/budgets/{budget_id}")
    fun deleteBudget(@Path("budget_id") budgetId: String): Single<ResponseBody>

    // Transactions

    @GET("api/budgets/{budget_id}/transactions")
    fun getBudgetTransactions(@Path("budget_id") budgetId: String): Single<Response<List<Transaction>>>

    @POST("api/budgets/{budget_id}/transactions")
    fun createBudgetTransaction(@Path("budget_id") budgetId: String, @Body request: TransactionRequest): Single<Unit>

    // Members

    @GET("api/budgets/{budget_id}/members")
    fun getBudgetMembers(@Path("budget_id") budgetId: String): Single<Response<List<Member>>>

    @POST("api/budgets/{budget_id}/members")
    fun createBudgetMember(@Path("budget_id") budgetId: String, @Body request: MemberRequest): Single<Unit>

    // JoinBudget

    @GET("api/budgets/code/{code}/members")
    fun getJoinBudgetMembers(@Path("code") code: String): Single<Response<List<Member>>>

    @PUT("api/budgets/code/{code}/members/{id}")
    fun joinBudget(@Path("code") code: String, @Path("id") id: String, @Body request: MemberRequest): Single<ResponseBody>
}
