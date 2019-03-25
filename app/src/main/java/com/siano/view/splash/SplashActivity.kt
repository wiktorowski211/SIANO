package com.siano.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.siano.view.main.RepositoriesActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(RepositoriesActivity.newIntent(this))
        finish()
    }
}
