package com.siano.view.budget

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacekmarchwicki.universaladapter.ViewHolderManager
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import com.siano.R
import com.siano.api.model.Transaction
import com.siano.base.AuthorizedActivity
import com.siano.base.BaseViewHolderManager
import com.siano.base.Rx2UniversalAdapter
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.annotations.Scope
import com.siano.dagger.module.BaseActivityModule
import com.siano.layoutmanager.MyLinearLayoutManager
import com.siano.utils.ErrorHandler
import com.siano.view.addMember.AddMemberActivity
import com.siano.view.editBudget.EditBudgetActivity
import com.siano.view.transaction.TransactionActivity
import dagger.Binds
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_budget.*
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import javax.inject.Inject
import javax.inject.Named

class BudgetActivity : AuthorizedActivity() {

    companion object {
        private const val EXTRA_BUDGET_ID = "budget_id"
        private const val MY_PERMISSIONS_REQUEST_WRITE_FILE = 1

        fun newIntent(context: Context, budgetId: Long) = Intent(context, BudgetActivity::class.java)
            .putExtra(EXTRA_BUDGET_ID, budgetId)
    }

    @Inject
    lateinit var presenter: BudgetPresenter

    private val subscription = CompositeDisposable()

    private val adapter = Rx2UniversalAdapter(
        listOf<ViewHolderManager>(
            BaseViewHolderManager(R.layout.item_budget_member, ::BudgetViewHolder, BudgetAdapterItem::class.java)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        setUpRecyclerView()

        budget_activity_toolbar.inflateMenu(R.menu.budget_menu)

        subscription.addAll(
            presenter.budgetObservable
                .subscribe { budget_activity_toolbar.title = it.name },
            presenter.itemsObservable
                .subscribe(adapter),
            presenter.deleteSuccessObservable()
                .subscribe { finish() },
            presenter.deleteErrorObservable()
                .subscribe(ErrorHandler.show(budget_main_view, this)),
            presenter.errorObservable
                .subscribe(ErrorHandler.show(budget_main_view, this)),
            budget_create_transaction_button.clicks()
                .subscribe { startActivity(TransactionActivity.newIntent(this, presenter.budgetId)) },
            budget_activity_toolbar.menu.findItem(R.id.budget_menu_delete).clicks()
                .switchMapSingle { presenter.deleteBudgetSingle() }
                .subscribe(),
            budget_activity_toolbar.menu.findItem(R.id.budget_menu_edit).clicks()
                .subscribe { startActivity(EditBudgetActivity.newIntent(this, presenter.budgetId)) },
            budget_activity_toolbar.menu.findItem(R.id.budget_menu_add_member).clicks()
                .subscribe { startActivity(AddMemberActivity.newIntent(this, presenter.budgetId)) },
            budget_activity_toolbar.menu.findItem(R.id.budget_menu_share).clicks()
                .subscribe { startActivity(shareBudget(this, presenter.budgetId)) },
            budget_activity_toolbar.menu.findItem(R.id.budget_generate_report).clicks()
                .switchMapSingle {
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    presenter.getReportSingle()
                }
                .subscribe(),
            presenter.getReportTransactionsObservable.subscribe(),
            budget_activity_toolbar.navigationClicks()
                .subscribe { finish() }
        )
    }

    private fun checkPermission(permission: String){
        if (ContextCompat.checkSelfPermission(this, permission) //Manifest.permission.WRITE_EXTERNAL_STORAGE
            != PackageManager.PERMISSION_GRANTED) {
            requestPermission(permission)
        }
    }

    private fun requestPermission(permission: String){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(permission),
                    MY_PERMISSIONS_REQUEST_WRITE_FILE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    private fun shareBudget(context: Context, budgetId: Long): Intent {
        val title = context.getText(R.string.budget_share).toString()
        val message = "http://sianoapp.gigalixirapp.com/budgets/$budgetId"

        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_SUBJECT, title)
            .putExtra(Intent.EXTRA_TEXT, message)

        return Intent.createChooser(intent, title)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createReport(context: Context, budgetId: Long): String {
        var document = PdfDocument()
        var data = presenter.getReportSingle().doOnSuccess { }




        return "Return"
    }

    private fun setUpRecyclerView() {
        budget_list.layoutManager = MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        budget_list.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    @dagger.Module(includes = [(BaseActivityModule::class)])
    abstract class Module {

        @Binds
        @DaggerAnnotation.ForActivity
        abstract fun provideActivity(activity: BudgetActivity): Activity

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            @Scope.Activity
            @Named("budgetId")
            fun provideBudgetId(activity: BudgetActivity): Long = activity.intent.let {
                it.data.toOption()
                    .flatMap { uri ->
                        uri.lastPathSegment.toOption()
                            .flatMap { id ->
                                id.toLongOrNull().toOption()
                            }
                    }.getOrElse {
                        it.getLongExtra(EXTRA_BUDGET_ID, 0)
                    }
            }
        }
    }
}
