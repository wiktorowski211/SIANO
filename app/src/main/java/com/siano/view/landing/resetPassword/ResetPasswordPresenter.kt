package com.siano.view.landing.resetPassword

import com.appunite.rx.dagger.UiScheduler
import com.siano.api.model.ResetPassword
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

class ResetPasswordPresenter @Inject constructor(
    key: String,
    private val authDao: AuthDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val passwordSubject = BehaviorSubject.createDefault("")
    private val resetSubject = PublishSubject.create<Unit>()

    private val passwordObservable: Observable<Either<DefaultError, String>> = passwordSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            it.length < 8 -> Either.left(PasswordToShortError)
            else -> Either.right(it)
        }
    }

    private val resetObservable: Observable<Either<DefaultError, Unit>> = resetSubject
        .withLatestFrom(passwordObservable) { _, password ->
            when {
                password.isLeft() -> Either.left(NotLoggedInError as DefaultError)
                else -> Either.right(ResetPassword.Password(password.right().get()))
            }
        }
        .switchMapRightWithEither { authDao.resetPasswordSingle(it, key) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    fun incorrectPasswordObservable(): Observable<Option<DefaultError>> = passwordObservable
        .skip(1)
        .mapToLeftOption()

    fun resetSuccessObservable(): Observable<Unit> = resetObservable
        .onlyRight()
        .map { Unit }

    fun resetFailedObservable(): Observable<Option<DefaultError>> = resetObservable
        .mapToLeftOption()

    fun passwordSingle(password: String): Single<Unit> = passwordSubject.executeFromSingle(password)

    fun resetSingle(): Single<Unit> = resetSubject.executeFromSingle(Unit)
}
