package com.siano.view.budget

import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.base.DefinedAdapterItem

data class BudgetAdapterItem(
    val name: String,
    val amount: Double
) : DefinedAdapterItem<Long> {
    override fun itemId(): Long = BaseAdapterItem.NO_ID
}