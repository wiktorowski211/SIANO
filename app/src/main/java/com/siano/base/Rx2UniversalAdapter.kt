package com.siano.base

import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.jacekmarchwicki.universaladapter.UniversalAdapter
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import io.reactivex.functions.Consumer
import rx.functions.Action1

open class Rx2UniversalAdapter(managers: List<ViewHolderManager>) : UniversalAdapter(managers), Consumer<List<BaseAdapterItem>>, Action1<List<BaseAdapterItem>> {

    override fun accept(t: List<BaseAdapterItem>) {
        call(t)
    }

}