package com.siano.base

import android.os.Bundle
import android.os.PersistableBundle
import com.siano.TokenPreferences
import com.siano.view.landing.LandingActivity
import javax.inject.Inject

abstract class AuthorizedActivity : BaseActivity() {

    @Inject
    lateinit var tokenPreferences: TokenPreferences

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        if (tokenPreferences.get().isEmpty()) {
            finish()
            startActivity(LandingActivity.newInstance(this))
        }
    }
}