package com.siano.view.main

import com.siano.api.model.Repository
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.base.DefinedAdapterItem
import com.siano.utils.executeFromSingle
import io.reactivex.Observer
import io.reactivex.Single

data class RepositoryAdapterItem(
    val repository: Repository,
    val itemClickObserver: Observer<Unit>
) : DefinedAdapterItem<Long> {
    override fun itemId(): Long = BaseAdapterItem.NO_ID

    fun itemClickSingle(): Single<Unit> = itemClickObserver.executeFromSingle(Unit)
}
