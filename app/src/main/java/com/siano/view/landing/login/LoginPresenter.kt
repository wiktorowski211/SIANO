package com.siano.view.landing.login

import com.appunite.rx.dagger.UiScheduler
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

class LoginPresenter @Inject constructor(
    private val authDao: AuthDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val usernameSubject = BehaviorSubject.createDefault("")
    private val passwordSubject = BehaviorSubject.createDefault("")
    private val loginSubject = PublishSubject.create<Unit>()

    private val usernameObservable: Observable<Either<DefaultError, String>> = usernameSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyError)
            else -> Either.right(it)
        }
    }

    private val passwordObservable: Observable<Either<DefaultError, String>> = passwordSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyError)
            else -> Either.right(it)
        }
    }

    val successObservable: Observable<Unit> = authDao.loginSuccessObservable().observeOn(uiScheduler)

    val errorObservable: Observable<Option<DefaultError>> = authDao.loginFailedObservable().observeOn(uiScheduler)

    val loginObservable: Observable<Unit> = loginSubject
        .withLatestFrom(usernameObservable.onlyRight(), passwordObservable.onlyRight()) { _, username, password ->
            TokenUtils.create(username, password)
        }
        .switchMapSingle { authDao.loginSingle(it) }

    fun usernameSingle(username: String): Single<Unit> = usernameSubject.executeFromSingle(username)
    fun passwordSingle(password: String): Single<Unit> = passwordSubject.executeFromSingle(password)
    fun loginSingle(): Single<Unit> = loginSubject.executeFromSingle(Unit)
}
