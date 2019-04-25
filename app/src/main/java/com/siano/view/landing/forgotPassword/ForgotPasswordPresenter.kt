package com.siano.view.landing.forgotPassword

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

class ForgotPasswordPresenter @Inject constructor(
    private val authDao: AuthDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val emailSubject = BehaviorSubject.createDefault("")
    private val resetSubject = PublishSubject.create<Unit>()

    private val emailObservable: Observable<Either<DefaultError, String>> = emailSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            else -> Either.right(it)
        }
    }

    private val resetObservable: Observable<Either<DefaultError, Unit>> = resetSubject
        .withLatestFrom(emailObservable) { _, email ->
            when {
                email.isLeft() -> Either.left(NotLoggedInError as DefaultError)
                else -> Either.right(ResetPassword.Email(email.right().get()))
            }
        }
        .switchMapRightWithEither { authDao.forgotPasswordSingle(it) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    fun incorrectEmailObservable(): Observable<Option<DefaultError>> = emailObservable
        .skip(1)
        .mapToLeftOption()

    fun resetSuccessObservable(): Observable<Unit> = resetObservable
        .onlyRight()
        .map { Unit }

    fun resetFailedObservable(): Observable<Option<DefaultError>> = resetObservable
        .mapToLeftOption()

    fun emailSingle(email: String): Single<Unit> = emailSubject.executeFromSingle(email)
    fun loginSingle(): Single<Unit> = resetSubject.executeFromSingle(Unit)
}
