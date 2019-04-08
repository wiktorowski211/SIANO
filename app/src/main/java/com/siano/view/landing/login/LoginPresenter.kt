package com.siano.view.landing.login

import com.appunite.rx.dagger.UiScheduler
import com.siano.dao.AuthDao
import com.siano.utils.DefaultError
import com.siano.utils.TokenUtils
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.funktionale.option.Option
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val authDao: AuthDao,
    @UiScheduler uiScheduler: Scheduler
) {
    val successObservable: Observable<Unit> = authDao.loginSuccessObservable().observeOn(uiScheduler)

    val errorObservable: Observable<Option<DefaultError>> = authDao.loginFailedObservable().observeOn(uiScheduler)

    fun loginSingle(username: String, password: String): Single<Unit> =
        authDao.loginSingle(TokenUtils.create(username, password))
}
