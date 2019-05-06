package com.siano.view.landing.login

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
import com.siano.view.budgets.BudgetsActivity
import com.siano.view.landing.forgotPassword.ForgotPasswordActivity
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, LoginActivity::class.java)
    }

    @Inject
    lateinit var presenter: LoginPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        subscription.addAll(
            username_edit_text.textChanges()
                .switchMapSingle { presenter.usernameSingle(it.toString()) }
                .subscribe(),
            password_edit_text.textChanges()
                .switchMapSingle { presenter.passwordSingle(it.toString()) }
                .subscribe(),
            login_activity_button.clicks()
                .switchMapSingle { presenter.loginSingle() }
                .subscribe(),
            login_activity_forgot_password.clicks()
                .subscribe {
                    startActivity(ForgotPasswordActivity.newInstance(this))
                },
            presenter.loginSuccessObservable()
                .subscribe {
                    startActivity(BudgetsActivity.newIntent(this).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                },
            presenter.loginFailedObservable()
                .subscribe(ErrorHandler.show(login_activity_main_view, this)),
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
        abstract fun provideActivity(activity: LoginActivity): Activity
    }
}