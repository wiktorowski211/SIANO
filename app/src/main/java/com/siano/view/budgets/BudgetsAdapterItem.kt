package com.siano.view.budgets

import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Budget
import com.siano.base.DefinedAdapterItem
import com.siano.utils.executeFromSingle
import io.reactivex.Observer
import io.reactivex.Single

data class BudgetAdapterItem(
    val budget: Budget,
    val itemClickObserver: Observer<Budget>
) : DefinedAdapterItem<Long> {
    override fun itemId(): Long = BaseAdapterItem.NO_ID

    fun itemClickSingle(budget: Budget): Single<Unit> = itemClickObserver.executeFromSingle(budget)
}