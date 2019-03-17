package com.siano.view.login

import com.siano.TokenPreferences
import com.siano.dao.AuthoDao
import com.siano.utils.TokenUtils
import com.appunite.rx.dagger.UiScheduler
import com.siano.utils.mapRight
import com.siano.utils.onlyLeft
import com.siano.utils.onlyRight
import com.siano.utils.DefaultError
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.funktionale.either.Either
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

    val errorObservable: Observable<DefaultError> = requestSignInObservable
        .onlyLeft()
}
