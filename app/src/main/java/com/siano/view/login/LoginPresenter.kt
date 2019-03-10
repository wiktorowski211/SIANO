package com.siano.view.login

import com.siano.TokenPreferences
import com.siano.dao.AuthoDao
import com.siano.utils.TokenUtils
import com.appunite.rx.dagger.UiScheduler
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class LoginPresenter @Inject constructor(@Named("SignInClickObservable") signInClickObservable: Observable<Pair<String, String>>,
                                         @UiScheduler uiScheduler: Scheduler,
                                         tokenPreferences: TokenPreferences,
                                         authoDao: AuthoDao
) {

    val requestSignInObservable = signInClickObservable
            .doOnNext { pair -> tokenPreferences.edit().setToken(TokenUtils.create(pair.first, pair.second)) }
            .switchMap { authoDao.authorizeUser() }
            .observeOn(uiScheduler)
            .replay()
            .refCount()
}
