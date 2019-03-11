package com.siano.dao

import com.siano.api.ApiService
import com.siano.api.model.Repository
import com.appunite.rx.dagger.NetworkScheduler
import com.siano.utils.DefaultError
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Scheduler
import io.reactivex.Single
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {

    fun getUserRepositoriesObservable(): Single<Either<DefaultError, List<Repository>>> =
        apiService.getUserRepositories()
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
}
