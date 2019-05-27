package com.siano.view.createBudget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.gms.ads.InterstitialAd
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.module.BaseActivityModule
import com.siano.base.AuthorizedActivity
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_create_budget.*
import javax.inject.Inject

class CreateBudgetActivity : AuthorizedActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, CreateBudgetActivity::class.java)
    }

    @Inject
    lateinit var presenter: CreateBudgetPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_budget)

        create_budget_activity_toolbar.inflateMenu(R.menu.create_budget_menu)

        subscription.addAll(
            create_budget_name.textChanges()
                .switchMapSingle { presenter.setBudgetNameSingle(it.toString()) }
                .subscribe(),
            create_budget_color.textChanges()
                .switchMapSingle { presenter.setBudgetColorSingle(it.toString()) }
                .subscribe(),
            color_picker.colorChanges()
                .subscribe { hex ->
                    create_budget_color.setText(hex)
                    under_color_picker_text.setTextColor(Color.parseColor(hex))
                },
            create_budget_activity_toolbar.menu.findItem(R.id.create_budget_menu_save).clicks()
                .switchMapSingle { presenter.saveBudgetSingle() }
                .subscribe(),
            presenter.canSaveObservable()
                .subscribe {
                    create_budget_activity_toolbar.menu.findItem(R.id.create_budget_menu_save).isVisible = it
                },
            presenter.saveBudgetObservable().subscribe { finish() },
            create_budget_activity_toolbar.navigationClicks()
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
        abstract fun provideActivity(activity: CreateBudgetActivity): Activity
    }
}

