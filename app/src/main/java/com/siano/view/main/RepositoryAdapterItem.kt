package com.siano.view.main

import com.siano.api.model.Repository
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.jacekmarchwicki.universaladapter.BaseAdapterItem.NO_ID
import io.reactivex.Observer

data class RepositoryAdapterItem(val repository: Repository,
                                 val itemClickObserver: Observer<Unit>
) : BaseAdapterItem {

    override fun same(item: BaseAdapterItem): Boolean = this == item

    override fun matches(item: BaseAdapterItem): Boolean = item is RepositoryAdapterItem && repository.id == item.repository.id

    override fun adapterId(): Long = NO_ID
}
