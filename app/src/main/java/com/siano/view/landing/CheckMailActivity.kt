package com.siano.view.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_check_mail.*

class CheckMailActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, CheckMailActivity::class.java)
    }

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_mail)

        subscription.addAll(
            check_email_button.clicks()
                .subscribe { finish() }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }
}