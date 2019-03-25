package com.siano.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.siano.view.budgets.BudgetsActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(BudgetsActivity.newIntent(this))
        finish()
    }
}
