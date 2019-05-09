package com.siano.view.joinBudget

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.siano.base.DefinedViewHolder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_join_budget.view.*

class JoinBudgetViewHolder(itemView: View) : DefinedViewHolder<JoinBudgetAdapterItem>(itemView) {

    private val itemClickObservable: Observable<Unit> = itemView.join_budget_body.clicks()

    override fun bindStatic(item: JoinBudgetAdapterItem) {
        itemView.join_budget_name.text = item.member.nickname
    }

    override fun bindDisposable(item: JoinBudgetAdapterItem) = CompositeDisposable(
        itemClickObservable
            .switchMapSingle { item.itemClickSingle(item.member) }
            .subscribe())
}
