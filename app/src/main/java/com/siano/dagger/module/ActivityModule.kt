package com.siano.dagger.module

import com.siano.dagger.annotations.Scope
import com.siano.view.login.LoginActivity
import com.siano.view.main.RepositoriesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(LoginActivity.Module::class)])
    abstract fun provideLoginActivity(): LoginActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(RepositoriesActivity.Module::class)])
    abstract fun provideMainActivity(): RepositoriesActivity

}
