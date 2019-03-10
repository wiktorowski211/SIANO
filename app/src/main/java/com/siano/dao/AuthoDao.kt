package com.siano.dao

import com.siano.api.ApiService
import com.appunite.rx.ResponseOrError
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthoDao @Inject constructor(
        private val apiService: ApiService
) {
    fun authorizeUser(): Observable<ResponseOrError<ResponseBody>> {
        return apiService.authorizeUser()
                .compose(ResponseOrError.toResponseOrErrorObservable())
    }
}