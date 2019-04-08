package com.siano.dagger.module

import com.siano.dagger.annotations.Scope
import com.siano.view.addMember.AddMemberActivity
import com.siano.view.budget.BudgetActivity
import com.siano.view.budgets.BudgetsActivity
import com.siano.view.createBudget.CreateBudgetActivity
import com.siano.view.editBudget.EditBudgetActivity
import com.siano.view.login.LoginActivity
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

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(BudgetActivity.Module::class)])
    abstract fun provideBudgetActivity(): BudgetActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(CreateBudgetActivity.Module::class)])
    abstract fun provideCreateBudgetActivity(): CreateBudgetActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(EditBudgetActivity.Module::class)])
    abstract fun provideEditBudgetActivity(): EditBudgetActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(AddMemberActivity.Module::class)])
    abstract fun provideAddMemberActivity(): AddMemberActivity
}
