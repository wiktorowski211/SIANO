package com.siano.view.landing.login

import com.appunite.rx.dagger.UiScheduler
import com.siano.TokenPreferences
import com.siano.api.model.AccessToken
import com.siano.api.model.Session
import com.siano.dao.AuthDao
import com.siano.utils.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import org.funktionale.option.toOption
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val authDao: AuthDao,
    private val tokenPreferences: TokenPreferences,
    @UiScheduler uiScheduler: Scheduler
) {
    private val usernameSubject = BehaviorSubject.createDefault("")
    private val passwordSubject = BehaviorSubject.createDefault("")
    private val loginSubject = PublishSubject.create<Unit>()

    private val usernameObservable: Observable<Either<DefaultError, String>> = usernameSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            else -> Either.right(it)
        }
    }

    private val passwordObservable: Observable<Either<DefaultError, String>> = passwordSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            else -> Either.right(it)
        }
    }

    private val loginObservable: Observable<Either<DefaultError, AccessToken>> = loginSubject
        .withLatestFrom(usernameObservable, passwordObservable) { _, username, password ->
            when {
                username.isLeft() || password.isLeft() -> Either.left(NotLoggedInError as DefaultError)
                else -> Either.right(Session.Username(username.right().get(), password.right().get()))
            }
        }
        .switchMapRightWithEither { authDao.authorizeUserSingle(it) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    fun incorrectUsernameObservable(): Observable<Option<DefaultError>> = usernameObservable
        .skip(1)
        .mapToLeftOption()

    fun incorrectPasswordObservable(): Observable<Option<DefaultError>> = passwordObservable
        .skip(1)
        .mapToLeftOption()

    fun loginSuccessObservable(): Observable<Unit> = loginObservable
        .onlyRight()
        .doOnNext { tokenPreferences.set(it.access_token) }
        .map { Unit }

    fun loginFailedObservable(): Observable<Option<DefaultError>> = loginObservable
        .onlyLeft()
        .map { it.toOption() }
        .doOnNext { tokenPreferences.clear() }

    fun usernameSingle(username: String): Single<Unit> = usernameSubject.executeFromSingle(username)
    fun passwordSingle(password: String): Single<Unit> = passwordSubject.executeFromSingle(password)
    fun loginSingle(): Single<Unit> = loginSubject.executeFromSingle(Unit)
}
