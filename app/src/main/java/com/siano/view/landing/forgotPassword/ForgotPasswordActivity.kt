package com.siano.view.landing.forgotPassword

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
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
import kotlinx.android.synthetic.main.activity_forgot_password.*
import javax.inject.Inject

class ForgotPasswordActivity : BaseActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, ForgotPasswordActivity::class.java)
    }

    @Inject
    lateinit var presenter: ForgotPasswordPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        MobileAds.initialize(this,
            "ca-app-pub-3940256099942544~3347511713")

        val bannerAd = InterstitialAd(this)
        bannerAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        bannerAd.loadAd(AdRequest.Builder().build())

        subscription.addAll(
            email_edit_text.textChanges()
                .switchMapSingle { presenter.emailSingle(it.toString()) }
                .subscribe(),
            forgot_password_activity_button.clicks()
                .switchMapSingle {
                    if (bannerAd.isLoaded) {
                        bannerAd.show()
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.")
                    }
                    presenter.resetSingle() }
                .subscribe(),
            presenter.resetSuccessObservable()
                .subscribe {
                    //finish()
                    //startActivity(CheckMailActivity.newInstance(this))
                },
            presenter.resetFailedObservable()
                .subscribe(ErrorHandler.show(forgot_password_activity_main_view, this)),
            presenter.incorrectEmailObservable()
                .subscribe {
                    email_edit_text.error = it.map { it.translate() }.orNull()
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
        abstract fun provideActivity(activity: ForgotPasswordActivity): Activity
    }
}