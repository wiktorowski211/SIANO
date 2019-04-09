package com.siano.view.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import com.siano.view.landing.login.LoginActivity
import com.siano.view.landing.registration.RegistrationActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, LandingActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        subscription.addAll(
            landing_activity_login_button.clicks()
                .subscribe { startActivity(LoginActivity.newInstance(this)) },
            landing_activity_register_button.clicks()
                .subscribe { startActivity(RegistrationActivity.newInstance(this)) }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }
}