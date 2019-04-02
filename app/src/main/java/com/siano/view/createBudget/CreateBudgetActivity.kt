package com.siano.view.createBudget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.module.BaseActivityModule
import com.siano.view.BaseActivity
import com.siano.view.main.CreateBudgetPresenter
import dagger.Binds
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_create_budget.*
import javax.inject.Inject

class CreateBudgetActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, CreateBudgetActivity::class.java)
    }


    private lateinit var bitmap: Bitmap

    @Inject
    lateinit var presenter: CreateBudgetPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_budget)

        create_budget_activity_toolbar.inflateMenu(R.menu.create_budget_menu)


        color_picker.isDrawingCacheEnabled = true
        color_picker.buildDrawingCache(true)

        color_picker.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                bitmap = color_picker.drawingCache
                val pixel = bitmap.getPixel(event.x.toInt(), event.y.toInt())

                val hex = "#" + Integer.toHexString(pixel).substring(2)

                create_budget_color.setText(hex)
                under_color_picker_text.setTextColor(Color.parseColor(hex))

            }
            true
        }



        subscription.addAll(
            create_budget_name.textChanges()
                .switchMapSingle { presenter.setBudgetNameSingle(it.toString()) }
                .subscribe(),
            create_budget_color.textChanges()
                .switchMapSingle { presenter.setBudgetColorSingle(it.toString()) }
                .subscribe(),
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

