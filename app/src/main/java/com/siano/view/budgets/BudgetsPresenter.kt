package com.siano.view.budgets

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Budget
import com.siano.dao.BudgetDao
import com.siano.utils.DefaultError
import com.siano.utils.mapToLeftOption
import com.siano.utils.onlyRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.funktionale.option.Option
import javax.inject.Inject

class BudgetsPresenter @Inject constructor(
    private val budgetDao: BudgetDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val openBudgetSubject = PublishSubject.create<Budget>()

    private val budgetsObservable = budgetDao.getBudgetsObservable()
        .observeOn(uiScheduler)

    val itemsObservable: Observable<List<BaseAdapterItem>> = budgetsObservable
        .onlyRight()
        .map { budgets -> budgets.map { BudgetAdapterItem(it, openBudgetSubject) } }

    val errorObservable: Observable<Option<DefaultError>> = budgetsObservable
        .mapToLeftOption()

    fun openBudgetObservable(): Observable<Budget> = openBudgetSubject

    fun refreshBudgetsObservable(): Single<Unit> = budgetDao.refreshBudgetsSingle()
}
