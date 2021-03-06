package com.siano.view.transaction

import android.view.View
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.api.model.TransactionShare
import com.siano.base.DefinedViewHolder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_transaction_share.view.*

class TransactionViewHolder(itemView: View) : DefinedViewHolder<TransactionAdapterItem>(itemView) {

    private val amountChangedObservable: Observable<Double> =
        itemView.transaction_share_amount.textChanges().map { it.toString().toDoubleOrNull() ?: 0.0 }

    override fun bindStatic(item: TransactionAdapterItem) {
        itemView.transaction_share_name.text = item.member.nickname
    }

    override fun bindDisposable(item: TransactionAdapterItem) = CompositeDisposable(
        amountChangedObservable
            .switchMapSingle { item.amountChangedSingle(TransactionShare(it, item.member.id)) }
            .subscribe())
}
