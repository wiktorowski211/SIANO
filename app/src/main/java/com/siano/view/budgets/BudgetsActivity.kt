package com.siano.view.budgets

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import com.siano.base.AuthorizedActivity
import com.siano.base.BaseViewHolderManager
import com.siano.base.Rx2UniversalAdapter
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.module.BaseActivityModule
import com.siano.layoutmanager.MyLinearLayoutManager
import com.siano.utils.ErrorHandler
import com.siano.view.budget.BudgetActivity
import com.siano.view.createBudget.CreateBudgetActivity
import com.siano.view.joinBudget.JoinBudgetActivity
import com.siano.view.landing.LandingActivity
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_budgets.*
import javax.inject.Inject

class BudgetsActivity : AuthorizedActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, BudgetsActivity::class.java)
    }

    @Inject
    lateinit var presenter: BudgetsPresenter

    private val subscription = CompositeDisposable()

    private val adapter = Rx2UniversalAdapter(
        listOf<ViewHolderManager>(
            BaseViewHolderManager(R.layout.item_budget, ::BudgetsViewHolder, BudgetAdapterItem::class.java)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budgets)

        budgets_activity_toolbar.inflateMenu(R.menu.budgets_menu)

        setUpRecyclerView()
        budgets_list_refresh_layout
        subscription.addAll(
            presenter.itemsObservable
                .subscribe(adapter),
            presenter.itemsObservable
                .subscribe { budgets_list_refresh_layout.isRefreshing = false },
            presenter.errorObservable
                .subscribe(ErrorHandler.show(budgets_main_view, this)),
            presenter.openBudgetObservable()
                .subscribe { startActivity(BudgetActivity.newIntent(this, it.id)) },
            budgets_create_budget_button.clicks()
                .subscribe { startActivity(CreateBudgetActivity.newIntent(this)) },
            budgets_activity_toolbar.menu.findItem(R.id.budgets_menu_join).clicks()
                .subscribe { startActivity(JoinBudgetActivity.newIntent(this)) },
            budgets_activity_toolbar.menu.findItem(R.id.budgets_menu_log_out).clicks()
                .subscribe {
                    tokenPreferences.clear()
                    startActivity(LandingActivity.newInstance(this).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                },
            budgets_list_refresh_layout.refreshes()
                .switchMapSingle { presenter.refreshBudgetsObservable() }
                .subscribe()
        )
    }

    private fun setUpRecyclerView() {
        budgets_list.layoutManager = MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        budgets_list.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: BudgetsActivity): Activity
    }
}
