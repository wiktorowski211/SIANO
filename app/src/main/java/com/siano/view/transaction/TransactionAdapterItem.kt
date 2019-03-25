package com.siano.view.transaction

import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.TransactionShare
import com.siano.base.DefinedAdapterItem
import com.siano.utils.executeFromSingle
import io.reactivex.Observer
import io.reactivex.Single

data class TransactionAdapterItem(
    val share: TransactionShare,
    private val amountChangedObserver: Observer<TransactionShare>
) : DefinedAdapterItem<Long> {
    override fun itemId(): Long = BaseAdapterItem.NO_ID
    fun amountChangedSingle(share:TransactionShare): Single<Unit> = amountChangedObserver.executeFromSingle(share)
}