package com.siano.dagger.module

import com.siano.dagger.annotations.Scope
import com.siano.view.login.LoginActivity
import com.siano.view.main.BudgetsActivity
import com.siano.view.main.RepositoriesActivity
import com.siano.view.transaction.TransactionActivity
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

    @Scope.Activity
    @ContributesAndroidInjector(modules = [TransactionActivity.Module::class, TransactionActivity.FragmentModule::class])
    abstract fun provideTransactionActivity(): TransactionActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(BudgetsActivity.Module::class)])
    abstract fun provideBudgetsActivity(): BudgetsActivity
}
