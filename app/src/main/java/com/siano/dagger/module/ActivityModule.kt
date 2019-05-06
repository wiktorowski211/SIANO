package com.siano.dagger.module

import com.siano.dagger.annotations.Scope
import com.siano.view.addMember.AddMemberActivity
import com.siano.view.budget.BudgetActivity
import com.siano.view.budgets.BudgetsActivity
import com.siano.view.createBudget.CreateBudgetActivity
import com.siano.view.editBudget.EditBudgetActivity
import com.siano.view.landing.login.LoginActivity
import com.siano.view.landing.forgotPassword.ForgotPasswordActivity
import com.siano.view.landing.resetPassword.ResetPasswordActivity
import com.siano.view.landing.registration.RegistrationActivity
import com.siano.view.transaction.TransactionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(LoginActivity.Module::class)])
    abstract fun provideLoginActivity(): LoginActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(RegistrationActivity.Module::class)])
    abstract fun provideRegistrationActivity(): RegistrationActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(ForgotPasswordActivity.Module::class)])
    abstract fun provideForgotPasswordActivity(): ForgotPasswordActivity

    @Scope.Activity
    @ContributesAndroidInjector(modules = [(ResetPasswordActivity.Module::class)])
    abstract fun provideResetPasswordActivity(): ResetPasswordActivity

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
