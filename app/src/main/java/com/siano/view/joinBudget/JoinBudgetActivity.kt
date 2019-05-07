package com.siano.view.joinBudget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.visibility
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.base.AuthorizedActivity
import com.siano.base.BaseViewHolderManager
import com.siano.base.Rx2UniversalAdapter
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.module.BaseActivityModule
import com.siano.layoutmanager.MyLinearLayoutManager
import com.siano.utils.ErrorHandler
import com.siano.utils.translate
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_join_budget.*
import javax.inject.Inject

class JoinBudgetActivity : AuthorizedActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, JoinBudgetActivity::class.java)
    }

    @Inject
    lateinit var presenter: JoinBudgetPresenter

    private val subscription = CompositeDisposable()

    private val adapter = Rx2UniversalAdapter(
        listOf<ViewHolderManager>(
            BaseViewHolderManager(R.layout.item_join_budget, ::JoinBudgetViewHolder, JoinBudgetAdapterItem::class.java)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_budget)

        setUpRecyclerView()

        subscription.addAll(
            presenter.itemsObservable
                .subscribe(adapter),
            presenter.joinBudgetSuccess
                .subscribe { finish() },
            presenter.errorObservable
                .subscribe(ErrorHandler.show(join_budget_main_view, this)),
            presenter.showInfoTextObservable
                .subscribe(join_budget_info.visibility(View.GONE)),
            join_budget_code_input.textChanges()
                .switchMapSingle { presenter.codeSingle(it.toString()) }
                .subscribe(),
            join_budget_code_button.clicks()
                .switchMapSingle { presenter.joinBudgetSingle() }
                .subscribe(),
            presenter.incorrectCodeObservable()
                .subscribe { join_budget_code_input.error = if (it.isDefined()) it.get().translate() else null }
        )
    }

    private fun setUpRecyclerView() {
        join_budget_members_list.layoutManager = MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        join_budget_members_list.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: JoinBudgetActivity): Activity
    }
}
