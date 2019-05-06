package com.siano.view.landing.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.base.BaseActivity
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.module.BaseActivityModule
import com.siano.utils.ErrorHandler
import com.siano.utils.translate
import com.siano.view.landing.CheckMailActivity
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_registration.*
import javax.inject.Inject

class RegistrationActivity : BaseActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, RegistrationActivity::class.java)
    }

    @Inject
    lateinit var presenter: RegistrationPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        subscription.addAll(
            email_edit_text.textChanges()
                .switchMapSingle { presenter.emailSingle(it.toString()) }
                .subscribe(),
            username_edit_text.textChanges()
                .switchMapSingle { presenter.usernameSingle(it.toString()) }
                .subscribe(),
            password_edit_text.textChanges()
                .switchMapSingle { presenter.passwordSingle(it.toString()) }
                .subscribe(),
            registration_activity_button.clicks()
                .switchMapSingle { presenter.loginSingle() }
                .subscribe(),
            presenter.registrationSuccessObservable()
                .subscribe {
                    finish()
                    startActivity(CheckMailActivity.newInstance(this))
                },
            presenter.registrationFailedObservable()
                .subscribe(ErrorHandler.show(registration_activity_main_view, this)),
            presenter.incorrectEmailObservable()
                .subscribe { email_edit_text.error = if (it.isDefined()) it.get().translate() else null },
            presenter.incorrectUsernameObservable()
                .subscribe { username_edit_text.error = if (it.isDefined()) it.get().translate() else null },
            presenter.incorrectPasswordObservable()
                .subscribe { password_edit_text.error = if (it.isDefined()) it.get().translate() else null }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: RegistrationActivity): Activity
    }
}