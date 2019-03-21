package com.siano.view.transaction

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.siano.R
import com.siano.view.transaction.fromWhom.FromWhomFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.transaction_activity.*

class TransactionActivity : AppCompatActivity() {

    private var subscription = SerialDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_activity)

        transaction_activity_toolbar.setNavigationOnClickListener { finish() }

        val transactionPagerAdapter = TransactionPagerAdapter(this, supportFragmentManager)
        transaction_activity_pager.adapter = transactionPagerAdapter
        transaction_activity_tab_layout.setupWithViewPager(transaction_activity_pager)

        subscription.set(
            CompositeDisposable(

            )
        )
    }

    override fun onDestroy() {
        subscription.set(Disposables.empty())
        super.onDestroy()
    }

    internal class TransactionPagerAdapter(private val context: Context, fm: FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            FROM_WHOM -> FromWhomFragment.newInstance()
            FOR_WHAT -> FromWhomFragment.newInstance()
            TO_WHOM -> FromWhomFragment.newInstance()
            else -> throw RuntimeException("Unknown position $position")
        }

        override fun getCount(): Int = TOTAL_COUNT

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            FROM_WHOM -> context.getString(R.string.from_whom)
            FOR_WHAT -> context.getString(R.string.for_what)
            TO_WHOM -> context.getString(R.string.to_whom)
            else -> throw RuntimeException("Unknown position $position")
        }

        companion object {

            const val TOTAL_COUNT = 3
            const val FROM_WHOM = 0
            const val FOR_WHAT = 1
            const val TO_WHOM = 2
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, TransactionActivity::class.java)
    }
}

