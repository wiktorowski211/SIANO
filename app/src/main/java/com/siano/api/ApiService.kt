package com.siano.api

import com.siano.api.model.Repository
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiService {

    @GET("/authorizations")
    fun authorizeUser(): Single<ResponseBody>

    @GET("/user/repos?affiliation=owner")
    fun getUserRepositories(): Single<List<Repository>>
}