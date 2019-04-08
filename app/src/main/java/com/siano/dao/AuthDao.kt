package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.TokenPreferences
import com.siano.api.ApiService
import com.siano.utils.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDao @Inject constructor(
    private val apiService: ApiService,
    private val tokenPreferences: TokenPreferences,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val authorizeSubject = BehaviorSubject.createDefault(tokenPreferences.get())

    private val authorizedObservable: Observable<Either<DefaultError, Unit>> = authorizeSubject
        .doOnNext { token -> tokenPreferences.set(token) }
        .switchMapSingle { apiService.authorizeUser().subscribeOn(networkScheduler).map { Unit } }
        .handleEitherRestErrors()
        .replay()
        .refCount()

    fun loginSuccessObservable(): Observable<Unit> = authorizedObservable
        .onlyRight()

    fun loginFailedObservable(): Observable<Option<DefaultError>> = authorizedObservable
        .mapToLeftOption()
        .doOnNext { if (it.isDefined()) tokenPreferences.clear() }

    fun loginSingle(token: String): Single<Unit> = authorizeSubject.executeFromSingle(token)
}