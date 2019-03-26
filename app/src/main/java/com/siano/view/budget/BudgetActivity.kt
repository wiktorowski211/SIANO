package com.siano.view.budget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import com.siano.TokenPreferences
import com.siano.base.BaseViewHolderManager
import com.siano.base.Rx2UniversalAdapter
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.annotations.Scope
import com.siano.dagger.module.BaseActivityModule
import com.siano.layoutmanager.MyLinearLayoutManager
import com.siano.utils.ErrorHandler
import com.siano.view.BaseActivity
import com.siano.view.login.LoginActivity
import com.siano.view.transaction.TransactionActivity
import dagger.Binds
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_budget.*
import javax.inject.Inject
import javax.inject.Named

class BudgetActivity : BaseActivity() {

    companion object {
        private const val EXTRA_BUDGET_ID = "budget_id"

        fun newIntent(context: Context, budgetId: Long) = Intent(context, BudgetActivity::class.java)
            .putExtra(EXTRA_BUDGET_ID, budgetId)
    }

    @Inject
    lateinit var tokenPreferences: TokenPreferences
    @Inject
    lateinit var presenter: BudgetPresenter

    private val subscription = CompositeDisposable()

    private val adapter = Rx2UniversalAdapter(
        listOf<ViewHolderManager>(
            BaseViewHolderManager(R.layout.item_budget_contact, ::BudgetViewHolder, BudgetAdapterItem::class.java)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        if (tokenPreferences.getToken().isEmpty()) {
            finish()
            startActivity(LoginActivity.newInstance(this))
        }

        setUpRecyclerView()

        budget_activity_toolbar.inflateMenu(R.menu.budget_menu)

        subscription.addAll(
            presenter.budgetObservable
                .subscribe { budget_activity_toolbar.title = it.name },
            presenter.itemsObservable
                .subscribe(adapter),
            presenter.deleteBudgetObservable
                .subscribe { finish() },
            presenter.errorObservable
                .subscribe(ErrorHandler.show(budget_main_view)),
            budget_create_transaction_button.clicks()
                .subscribe { startActivity(TransactionActivity.newIntent(this, presenter.budgetId)) },
            budget_activity_toolbar.menu.findItem(R.id.budget_menu_delete).clicks()
                .switchMapSingle { presenter.deleteBudgetSingle() }
                .subscribe(),
            budget_activity_toolbar.navigationClicks()
                .subscribe { finish() }
        )
    }

    private fun setUpRecyclerView() {
        budget_list.layoutManager = MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        budget_list.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: BudgetActivity): Activity

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            @Scope.Activity
            @Named("budgetId")
            fun provideBudgetId(activity: BudgetActivity): Long =
                checkNotNull(activity.intent.getLongExtra(EXTRA_BUDGET_ID, 0))
        }
    }
}
