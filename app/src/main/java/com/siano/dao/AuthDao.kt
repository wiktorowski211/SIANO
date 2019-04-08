package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.AccessToken
import com.siano.api.model.Session
import com.siano.api.model.SessionRequest
import com.siano.utils.DefaultError
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    fun authorizeUserSingle(session: Session): Observable<Either<DefaultError, AccessToken>> =
        apiService.authorizeUser(SessionRequest(session))
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .toObservable()
}