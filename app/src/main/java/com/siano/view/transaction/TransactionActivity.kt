package com.siano.view.transaction

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.annotations.Scope
import com.siano.dagger.module.BaseActivityModule
import com.siano.utils.ErrorHandler
import com.siano.utils.pageSelectChanges
import com.siano.base.AuthorizedActivity
import com.siano.view.transaction.forWhat.ForWhatFragment
import com.siano.view.transaction.fromWhom.FromWhomFragment
import com.siano.view.transaction.toWhom.ToWhomFragment
import dagger.Binds
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.activity_transaction.*
import javax.inject.Inject
import javax.inject.Named


class TransactionActivity : AuthorizedActivity() {

    @Inject
    lateinit var presenter: TransactionPresenter

    private var subscription = SerialDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        transaction_activity_toolbar.inflateMenu(R.menu.transaction_menu)

        val transactionPagerAdapter = TransactionPagerAdapter(this, supportFragmentManager)
        transaction_activity_pager.adapter = transactionPagerAdapter
        transaction_activity_pager.offscreenPageLimit = 3
        transaction_activity_tab_layout.setupWithViewPager(transaction_activity_pager)

        subscription.set(
            CompositeDisposable(
                presenter.successObservable()
                    .subscribe { finish() },
                presenter.showNextButtonObservable
                    .subscribe {
                        transaction_activity_toolbar.menu.findItem(R.id.transaction_menu_next).isVisible = it
                    },
                presenter.showSaveButtonObservable
                    .subscribe {
                        transaction_activity_toolbar.menu.findItem(R.id.transaction_menu_save).isVisible = it
                    },
                presenter.errorObservable()
                    .subscribe(ErrorHandler.show(transaction_activity_pager)),
                transaction_activity_toolbar.menu.findItem(R.id.transaction_menu_save).clicks()
                    .switchMapSingle { presenter.saveTransactionSingle() }
                    .subscribe(),
                transaction_activity_toolbar.menu.findItem(R.id.transaction_menu_next).clicks()
                    .subscribe { transaction_activity_pager.currentItem = transaction_activity_pager.currentItem + 1 },
                transaction_activity_pager.pageSelectChanges()
                    .switchMapSingle { position -> presenter.selectedPageChangedSingle(position) }
                    .subscribe(),
                transaction_activity_toolbar.navigationClicks()
                    .subscribe { finish() }
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
            FOR_WHAT -> ForWhatFragment.newInstance()
            TO_WHOM -> ToWhomFragment.newInstance()
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
        private const val EXTRA_BUDGET_ID = "budget_id"

        fun newIntent(context: Context, budgetId: Long) = Intent(context, TransactionActivity::class.java)
            .putExtra(EXTRA_BUDGET_ID, budgetId)
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: TransactionActivity): Activity

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            @Scope.Activity
            @Named("budgetId")
            fun provideBudgetId(activity: TransactionActivity): Long =
                checkNotNull(activity.intent.getLongExtra(TransactionActivity.EXTRA_BUDGET_ID, 0))
        }
    }

    @dagger.Module
    @Scope.Activity
    abstract class FragmentModule {
        @ContributesAndroidInjector
        internal abstract fun bindForWhatFragment(): ForWhatFragment

        @ContributesAndroidInjector
        internal abstract fun bindFromWhomFragment(): FromWhomFragment

        @ContributesAndroidInjector
        internal abstract fun bindToWhomFragment(): ToWhomFragment
    }
}

