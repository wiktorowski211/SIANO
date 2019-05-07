package com.siano.view.editBudget

import com.appunite.rx.dagger.UiScheduler
import com.siano.api.model.Budget
import com.siano.dao.BudgetDao
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.onlyRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Named

class EditBudgetPresenter @Inject constructor(
    private val budgetDao: BudgetDao,
    @Named("budgetId") val budgetId: Long,
    @UiScheduler val uiScheduler: Scheduler
) {
    private val budgetNameSubject = BehaviorSubject.create<String>()
    private val budgetColorSubject = BehaviorSubject.create<String>()

    private val saveBudgetSubject = PublishSubject.create<Unit>()

    private val findBudgetObservable: Observable<Either<DefaultError, Budget>> = budgetDao.getBudgetObservable(budgetId)
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    private val budgetObservable = findBudgetObservable
        .onlyRight()
        .replay()
        .refCount()

    val budgetNameObservable: Observable<String> = budgetObservable.map { it.name }

    val budgetColorObservable: Observable<String> = budgetObservable.map { it.color }

    fun canSaveObservable(): Observable<Boolean> = budgetNameObservable.map { it.isNotBlank() }

    fun saveBudgetObservable(): Observable<Unit> = saveBudgetSubject
        .withLatestFrom(
            budgetNameSubject,
            budgetColorSubject,
            budgetObservable
        ) { _, name, color, budget ->
            Budget(budget.id, name, "", color, budget.owner_id)
        }
        .switchMap { budgetDao.editBudgetSingle(it).map { Unit } }
        .observeOn(uiScheduler)

    fun setBudgetNameSingle(name: String): Single<Unit> = budgetNameSubject.executeFromSingle(name)

    fun setBudgetColorSingle(color: String): Single<Unit> = budgetColorSubject.executeFromSingle(color)

    fun saveBudgetSingle(): Single<Unit> = saveBudgetSubject.executeFromSingle(Unit)
}
