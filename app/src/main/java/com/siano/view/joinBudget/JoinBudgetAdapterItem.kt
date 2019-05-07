package com.siano.view.joinBudget

import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Member
import com.siano.base.DefinedAdapterItem
import com.siano.utils.executeFromSingle
import io.reactivex.Observer
import io.reactivex.Single

data class JoinBudgetAdapterItem(
    val member: Member,
    val itemClickObserver: Observer<Member>
) : DefinedAdapterItem<Long> {
    override fun itemId(): Long = BaseAdapterItem.NO_ID

    fun itemClickSingle(member: Member): Single<Unit> = itemClickObserver.executeFromSingle(member)
}