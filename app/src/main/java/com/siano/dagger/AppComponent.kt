package com.siano.dagger

import com.siano.App
import com.siano.dagger.module.ActivityModule
import com.siano.dagger.module.AndroidModule
import com.siano.dagger.module.AppModule
import com.siano.dagger.module.NetworkModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class), (ActivityModule::class), (NetworkModule::class), (AppModule::class), (AndroidModule::class)])
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}
