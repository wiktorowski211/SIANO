package com.siano.view.main

import android.view.View
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryViewHolder(itemView: View) : ViewHolderManager.BaseViewHolder<RepositoryAdapterItem>(itemView) {

    override fun bind(item: RepositoryAdapterItem) {
        itemView.repo_name.text = item.repository.name
        itemView.repo_description.text = item.repository.description

        itemView.repo_body.setOnClickListener {
            item.itemClickObserver.onNext(Unit)
        }
    }
}
