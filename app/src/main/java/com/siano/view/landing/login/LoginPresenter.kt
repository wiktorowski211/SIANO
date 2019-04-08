package com.siano.view.landing.login

import com.siano.TokenPreferences
import com.siano.dao.AuthoDao
import com.appunite.rx.dagger.UiScheduler
import com.siano.utils.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject
import javax.inject.Named

class LoginPresenter @Inject constructor(
    @Named("SignInClickObservable") signInClickObservable: Observable<Pair<String, String>>,
    @UiScheduler uiScheduler: Scheduler,
    tokenPreferences: TokenPreferences,
    authoDao: AuthoDao
) {

    private val requestSignInObservable: Observable<Either<DefaultError, Unit>> = signInClickObservable
        .doOnNext { pair -> tokenPreferences.edit().setToken(TokenUtils.create(pair.first, pair.second)) }
        .switchMapSingle { authoDao.authorizeUser() }
        .mapRight { Unit }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    val successObservable: Observable<Unit> = requestSignInObservable
        .onlyRight()

    val errorObservable: Observable<Option<DefaultError>> = requestSignInObservable
        .mapToLeftOption()
}
