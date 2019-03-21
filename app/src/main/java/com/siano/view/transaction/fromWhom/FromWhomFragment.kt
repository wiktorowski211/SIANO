package com.siano.view.transaction.fromWhom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.siano.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable

class FromWhomFragment : Fragment() {

    companion object {
        fun newInstance(): FromWhomFragment = FromWhomFragment()
    }

    private val subscription = SerialDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.for_whom_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscription.set(CompositeDisposable(

        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.set(Disposables.empty())
    }
}
