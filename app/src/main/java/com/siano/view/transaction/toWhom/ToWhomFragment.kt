package com.siano.view.transaction.toWhom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.siano.R
import com.siano.base.BaseViewHolderManager
import com.siano.base.Rx2UniversalAdapter
import com.siano.layoutmanager.MyLinearLayoutManager
import com.siano.view.BaseFragment
import com.siano.view.transaction.TransactionAdapterItem
import com.siano.view.transaction.TransactionPresenter
import com.siano.view.transaction.TransactionViewHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.fragment_to_whom.*
import javax.inject.Inject

class ToWhomFragment : BaseFragment() {

    companion object {
        fun newInstance(): ToWhomFragment = ToWhomFragment()
    }

    @Inject
    lateinit var presenter: TransactionPresenter

    private val subscription = SerialDisposable()

    private val adapter = Rx2UniversalAdapter(
        listOf<ViewHolderManager>(
            BaseViewHolderManager(
                R.layout.item_transaction_share, ::TransactionViewHolder, TransactionAdapterItem::class.java
            )
        )
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_to_whom, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        subscription.set(
            CompositeDisposable(
                presenter.toWhomItemsObservable
                    .subscribe(adapter),
                presenter.remainingAmountObservable
                    .subscribe {
                        to_whom_amount.text = context?.getString(R.string.transaction_remaining_amount, it.toString())
                    }
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Disposables.empty())
    }

    private fun setUpRecyclerView() {
        to_whom_list.layoutManager = MyLinearLayoutManager(this.context!!, LinearLayoutManager.VERTICAL, false)
        to_whom_list.adapter = adapter
    }
}