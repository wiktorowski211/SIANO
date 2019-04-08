package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.extractResponse
import com.siano.api.model.Budget
import com.siano.api.model.BudgetRequest
import com.siano.utils.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.BehaviorSubject
import org.funktionale.either.Either
import org.funktionale.option.toOption
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val refreshBudgetsSubject: BehaviorSubject<Unit> = BehaviorSubject.createDefault(Unit)

    private val budgetsObservable: Observable<Either<DefaultError, List<Budget>>> = refreshBudgetsSubject
        .switchMapSingle { apiService.getUserBudgets().subscribeOn(networkScheduler) }
        .handleEitherRestErrors()
        .extractResponse()
        .replay()
        .refCount()

    fun getBudgetsObservable(): Observable<Either<DefaultError, List<Budget>>> = budgetsObservable

    fun getBudgetObservable(budgetId: Long): Observable<Either<DefaultError, Budget>> = budgetsObservable
        .mapRightWithEither {
            it
                .firstOrNull { it.id == budgetId }
                .toOption()
                .fold({ Either.left(NotFoundError) }, { Either.right(it) })
        }

    fun createBudgetSingle(budget: Budget): Observable<Either<DefaultError, Unit>> =
        apiService.createBudget(BudgetRequest(budget))
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .toObservable()
            .switchMapRight { refreshBudgetsSubject.executeFromSingle(Unit).toObservable() }

    fun deleteBudgetSingle(budgetId: Long): Observable<Either<DefaultError, Unit>> =
        apiService.deleteBudget(budgetId.toString())
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .toObservable()
            .switchMapRight { refreshBudgetsSubject.executeFromSingle(Unit).toObservable() }
}
