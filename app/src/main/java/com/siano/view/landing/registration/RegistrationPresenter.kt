package com.siano.view.landing.registration

import com.appunite.rx.dagger.UiScheduler
import com.siano.api.model.User
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
import javax.inject.Inject

class RegistrationPresenter @Inject constructor(
    private val authDao: AuthDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val emailSubject = BehaviorSubject.createDefault("")
    private val usernameSubject = BehaviorSubject.createDefault("")
    private val passwordSubject = BehaviorSubject.createDefault("")
    private val registerSubject = PublishSubject.create<Unit>()

    private val emailObservable: Observable<Either<DefaultError, String>> = emailSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            else -> Either.right(it)
        }
    }

    private val usernameObservable: Observable<Either<DefaultError, String>> = usernameSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            else -> Either.right(it)
        }
    }

    private val passwordObservable: Observable<Either<DefaultError, String>> = passwordSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            it.length < 8 -> Either.left(PasswordToShortError)
            else -> Either.right(it)
        }
    }

    private val registrationObservable: Observable<Either<DefaultError, Unit>> = registerSubject
        .withLatestFrom(emailObservable, usernameObservable, passwordObservable) { _, email, username, password ->
            when {
                email.isLeft() || username.isLeft() || password.isLeft() -> Either.left(NotLoggedInError as DefaultError)
                else -> Either.right(User(0, username.right().get(), password.right().get(), email.right().get()))
            }
        }
        .switchMapRightWithEither { authDao.registerUserSingle(it) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    fun incorrectEmailObservable(): Observable<Option<DefaultError>> = emailObservable
        .skip(1)
        .mapToLeftOption()

    fun incorrectUsernameObservable(): Observable<Option<DefaultError>> = usernameObservable
        .skip(1)
        .mapToLeftOption()

    fun incorrectPasswordObservable(): Observable<Option<DefaultError>> = passwordObservable
        .skip(1)
        .mapToLeftOption()

    fun registrationSuccessObservable(): Observable<Unit> = registrationObservable
        .onlyRight()
        .map { Unit }

    fun registrationFailedObservable(): Observable<Option<DefaultError>> = registrationObservable
        .mapToLeftOption()

    fun emailSingle(email: String): Single<Unit> = emailSubject.executeFromSingle(email)
    fun usernameSingle(username: String): Single<Unit> = usernameSubject.executeFromSingle(username)
    fun passwordSingle(password: String): Single<Unit> = passwordSubject.executeFromSingle(password)
    fun loginSingle(): Single<Unit> = registerSubject.executeFromSingle(Unit)
}
