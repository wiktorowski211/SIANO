package com.siano

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.siano.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import rx.plugins.RxJavaHooks

class App : DaggerApplication(){

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        rxThrowableCatcher()
    }

    private fun rxThrowableCatcher() {
        RxJavaHooks.setOnError { throwable -> Log.e("RxError", " Message: ", throwable) }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder().create(this)
}