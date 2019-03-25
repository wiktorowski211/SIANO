package com.siano.base

import com.jacekmarchwicki.universaladapter.BaseAdapterItem

interface DefinedAdapterItem<out T : Any> : BaseAdapterItem {
    fun itemId(): T
    override fun adapterId(): Long = itemId().hashCode().toLong()
    override fun matches(item: BaseAdapterItem): Boolean = when (item) {
        is DefinedAdapterItem<*> -> itemId() == item.itemId()
        else -> false
    }
    override fun same(item: BaseAdapterItem): Boolean = this == item
}