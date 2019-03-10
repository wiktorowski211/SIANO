package com.siano.api

import com.siano.api.model.Repository
import okhttp3.ResponseBody
import retrofit2.http.GET
import rx.Observable

interface ApiService {

    @GET("/authorizations")
    fun authorizeUser(): Observable<ResponseBody>

    @GET("/user/repos?affiliation=owner")
    fun getUserRepositories(): Observable<List<Repository>>
}
