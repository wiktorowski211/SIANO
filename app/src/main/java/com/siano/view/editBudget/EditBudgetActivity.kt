package com.siano.view.editBudget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.siano.R
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.annotations.Scope
import com.siano.dagger.module.BaseActivityModule
import com.siano.view.BaseActivity
import dagger.Binds
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_edit_budget.*
import javax.inject.Inject
import javax.inject.Named

class EditBudgetActivity : BaseActivity() {

    companion object {
        private const val EXTRA_BUDGET_ID = "budget_id"

        fun newIntent(context: Context, budgetId: Long) = Intent(context, EditBudgetActivity::class.java)
            .putExtra(EXTRA_BUDGET_ID, budgetId)
    }

    private lateinit var bitmap: Bitmap

    @Inject
    lateinit var presenter: EditBudgetPresenter

    private val subscription = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        edit_budget_activity_toolbar.inflateMenu(R.menu.edit_budget_menu)

        color_picker.isDrawingCacheEnabled = true
        color_picker.buildDrawingCache(true)

        color_picker.setOnTouchListener { v, event ->
            bitmap = color_picker.drawingCache
            val width = bitmap.width
            val height = bitmap.height

            try {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {

                    if (event.x in 0..width && event.y in 0..height) {
                        val pixel = bitmap.getPixel(event.x.toInt(), event.y.toInt())

                        val hex = "#" + Integer.toHexString(pixel).substring(2)

                        edit_budget_color.setText(hex)
                        under_color_picker_text.setTextColor(Color.parseColor(hex))
                    }

                    true
                } else false
            } catch (e: Exception) {
                Log.wtf("zxc", e.message)
                false
            }
        }

        subscription.addAll(
            presenter.budgetNameObservable
                .subscribe { edit_budget_name.setText(it) },
            presenter.budgetColorObservable
                .subscribe { edit_budget_color.setText(it) },
            edit_budget_name.textChanges()
                .switchMapSingle { presenter.setBudgetNameSingle(it.toString()) }
                .subscribe(),
            edit_budget_color.textChanges()
                .switchMapSingle { presenter.setBudgetColorSingle(it.toString()) }
                .subscribe(),
            edit_budget_activity_toolbar.menu.findItem(R.id.edit_budget_menu_save).clicks()
                .switchMapSingle { presenter.saveBudgetSingle() }
                .subscribe(),
            presenter.canSaveObservable()
                .subscribe { edit_budget_activity_toolbar.menu.findItem(R.id.edit_budget_menu_save).isVisible = it },
            presenter.saveBudgetObservable().subscribe { finish() },
            edit_budget_activity_toolbar.navigationClicks()
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
        abstract fun provideActivity(activity: EditBudgetActivity): Activity

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            @Scope.Activity
            @Named("budgetId")
            fun provideBudgetId(activity: EditBudgetActivity): Long =
                checkNotNull(activity.intent.getLongExtra(EditBudgetActivity.EXTRA_BUDGET_ID, 0))
        }
    }
}
