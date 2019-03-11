package com.siano.dao

import com.siano.api.ApiService
import com.appunite.rx.dagger.NetworkScheduler
import com.siano.utils.DefaultError
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Scheduler
import io.reactivex.Single
import okhttp3.ResponseBody
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthoDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    fun authorizeUser(): Single<Either<DefaultError, ResponseBody>> = apiService.authorizeUser()
        .subscribeOn(networkScheduler)
        .handleEitherRestErrors()
}