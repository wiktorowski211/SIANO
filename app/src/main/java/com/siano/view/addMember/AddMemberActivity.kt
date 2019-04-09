package com.siano.view.addMember

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.base.AuthorizedActivity
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.annotations.Scope
import com.siano.dagger.module.BaseActivityModule
import dagger.Binds
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_add_member.*
import javax.inject.Inject
import javax.inject.Named

class AddMemberActivity : AuthorizedActivity() {

    companion object {
        private const val EXTRA_BUDGET_ID = "budget_id"

        fun newIntent(context: Context, budgetId: Long) = Intent(context,AddMemberActivity::class.java)
            .putExtra(EXTRA_BUDGET_ID, budgetId)
    }

    @Inject
    lateinit var presenter: AddMemberPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        add_member_activity_toolbar.inflateMenu(R.menu.add_member_menu)

        subscription.addAll(
          add_member_nickname.textChanges()
                .switchMapSingle { presenter.setMemberNameSingle(it.toString()) }
                .subscribe(),
            add_member_activity_toolbar.menu.findItem(R.id.add_member_menu_save).clicks()
                .switchMapSingle { presenter.saveMemberSingle() }
                .subscribe(),
            presenter.canSaveObservable()
                .subscribe { add_member_activity_toolbar.menu.findItem(R.id.add_member_menu_save).isVisible = it },
            presenter.saveBudgetObservable().subscribe { finish() },
            add_member_activity_toolbar.navigationClicks()
                .subscribe { finish() }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: AddMemberActivity): Activity

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            @Scope.Activity
            @Named("budgetId")
            fun provideBudgetId(activity: AddMemberActivity): Long =
                checkNotNull(activity.intent.getLongExtra(AddMemberActivity.EXTRA_BUDGET_ID, 0))
        }
    }
}
