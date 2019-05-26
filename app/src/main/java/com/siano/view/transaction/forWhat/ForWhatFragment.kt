package com.siano.view.transaction.forWhat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding3.widget.itemSelections
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.base.BaseFragment
import com.siano.view.transaction.TransactionPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.fragment_for_what.*
import javax.inject.Inject


class ForWhatFragment : BaseFragment() {

    companion object {
        fun newInstance(): ForWhatFragment = ForWhatFragment()
    }

    @Inject
    lateinit var presenter: TransactionPresenter

    private val subscription = SerialDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_for_what, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var categoriesArray = resources.getStringArray(R.array.categories)

        val adapterArrayCategories = ArrayAdapter(context, android.R.layout.simple_spinner_item, categoriesArray)
        adapterArrayCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        transaction_category.adapter = adapterArrayCategories

        subscription.set(
            CompositeDisposable(
                transaction_title.textChanges()
                    .switchMapSingle { presenter.titleChangedSingle(it.toString()) }
                    .subscribe(),
                transaction_category.itemSelections()
                    .map { position -> position }
                    .switchMapSingle { presenter.categoryChangedSingle(it + 1) }
                    .subscribe()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Disposables.empty())
    }
}
