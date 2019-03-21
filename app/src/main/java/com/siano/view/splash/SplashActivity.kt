package com.siano.view.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.siano.view.transaction.TransactionActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(TransactionActivity.newIntent(this))
        finish()
    }
}
