package com.siano.view.budgets

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.siano.base.DefinedViewHolder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_budget.view.*

class BudgetsViewHolder(itemView: View) : DefinedViewHolder<BudgetAdapterItem>(itemView) {

    private val itemClickObservable: Observable<Unit> = itemView.budget_body.clicks()

    override fun bindStatic(item: BudgetAdapterItem) {
        itemView.budget_name.text = item.budget.name
    }

    override fun bindDisposable(item: BudgetAdapterItem) = CompositeDisposable(
        itemClickObservable
            .switchMapSingle { item.itemClickSingle(item.budget) }
            .subscribe())
}
