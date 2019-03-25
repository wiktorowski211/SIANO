package com.siano.view.budgets

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import com.siano.TokenPreferences
import com.siano.base.BaseViewHolderManager
import com.siano.base.Rx2UniversalAdapter
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.module.BaseActivityModule
import com.siano.layoutmanager.MyLinearLayoutManager
import com.siano.utils.ErrorHandler
import com.siano.view.BaseActivity
import com.siano.view.createBudget.CreateBudgetActivity
import com.siano.view.login.LoginActivity
import com.siano.view.main.BudgetsPresenter
import com.siano.view.transaction.TransactionActivity
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_budgets.*
import javax.inject.Inject

class BudgetsActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, BudgetsActivity::class.java)
    }

    @Inject
    lateinit var tokenPreferences: TokenPreferences
    @Inject
    lateinit var presenter: BudgetsPresenter

    private val subscription = CompositeDisposable()

    private val adapter = Rx2UniversalAdapter(
        listOf<ViewHolderManager>(
            BaseViewHolderManager(R.layout.item_budget, ::BudgetViewHolder, BudgetAdapterItem::class.java)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budgets)

        if (tokenPreferences.getToken().isEmpty()) {
            finish()
            startActivity(LoginActivity.newInstance(this))
        }

        setUpRecyclerView()

        subscription.addAll(
            presenter.itemsObservable
                .subscribe(adapter),
            presenter.errorObservable
                .subscribe(ErrorHandler.show(budgets_main_view)),
            presenter.openBudgetObservable().subscribe { startActivity(TransactionActivity.newIntent(this)) },
            budgets_create_budget_button.clicks()
                .subscribe { startActivity(CreateBudgetActivity.newIntent(this)) }
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
