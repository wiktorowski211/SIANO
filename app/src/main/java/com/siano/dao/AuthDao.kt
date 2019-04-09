package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.*
import com.siano.utils.DefaultError
import com.siano.utils.handleEitherRestErrors
import com.siano.utils.mapRight
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

    fun registerUserSingle(user: User): Observable<Either<DefaultError, Unit>> =
        apiService.registerUser(UserRequest(user))
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .mapRight { Unit }
            .toObservable()
}