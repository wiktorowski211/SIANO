package com.siano.view.landing.resetPassword

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
import kotlinx.android.synthetic.main.activity_reset_password.*
import javax.inject.Inject

class ResetPasswordActivity : BaseActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, ResetPasswordActivity::class.java)
    }

    @Inject
    lateinit var presenter: ResetPasswordPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        subscription.addAll(
            password_edit_text.textChanges()
                .switchMapSingle { presenter.passwordSingle(it.toString()) }
                .subscribe(),
            reset_password_activity_button.clicks()
                .switchMapSingle { presenter.resetSingle() }
                .subscribe(),
            presenter.resetSuccessObservable()
                .subscribe {
                    finish()
                    startActivity(CheckMailActivity.newInstance(this))
                },
            presenter.resetFailedObservable()
                .subscribe(ErrorHandler.show(reset_password_activity_main_view, this)),
            presenter.incorrectPasswordObservable()
                .subscribe {
                    password_edit_text.error = it.map { it.translate() }.orNull()
                }
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
        abstract fun provideActivity(activity: ResetPasswordActivity): Activity
    }
}