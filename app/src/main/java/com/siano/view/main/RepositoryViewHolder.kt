package com.siano.view.main

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.siano.base.DefinedViewHolder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryViewHolder(itemView: View) : DefinedViewHolder<RepositoryAdapterItem>(itemView) {

    private val itemClickObservable: Observable<Unit> = itemView.repo_body.clicks()

    override fun bindStatic(item: RepositoryAdapterItem) {
        itemView.repo_name.text = item.repository.name
        itemView.repo_description.text = item.repository.description
    }

    override fun bindDisposable(item: RepositoryAdapterItem) = CompositeDisposable(
        itemClickObservable
            .switchMapSingle { item.itemClickSingle() }
            .subscribe())
}
