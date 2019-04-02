package com.siano.view.main

import com.appunite.rx.dagger.UiScheduler
import com.siano.api.model.Budget
import com.siano.dao.BudgetDao
import com.siano.utils.executeFromSingle
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class CreateBudgetPresenter @Inject constructor(
    val budgetsDao: BudgetDao,
    @UiScheduler uiScheduler: Scheduler
) {

    private val budgetNameSubject = BehaviorSubject.createDefault("")
    private val budgetColorSubject = BehaviorSubject.createDefault("#333333")

    private val saveBudgetSubject = PublishSubject.create<Unit>()

    fun canSaveObservable(): Observable<Boolean> = budgetNameSubject.map { it.isNotBlank() }

    fun saveBudgetObservable(): Observable<Unit> = saveBudgetSubject
        .withLatestFrom(budgetNameSubject, budgetColorSubject) { _, name, color -> Budget(6, name, color, 1) }
        .switchMapSingle { budgetsDao.addBudgetSingle(it) }

    fun setBudgetNameSingle(name: String): Single<Unit> = budgetNameSubject.executeFromSingle(name)

    fun setBudgetColorSingle(color: String): Single<Unit> = budgetColorSubject.executeFromSingle(color)

    fun saveBudgetSingle(): Single<Unit> = saveBudgetSubject.executeFromSingle(Unit)
}
