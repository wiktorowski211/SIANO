package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.Budget
import com.siano.api.model.BudgetUser
import com.siano.utils.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.ReplaySubject
import org.funktionale.either.Either
import org.funktionale.option.toOption
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val addBudgetSubject: ReplaySubject<Budget> = ReplaySubject.create()
    private val deleteBudgetSubject: ReplaySubject<Budget> = ReplaySubject.create()

    private val storedItems: Observable<List<Budget>> = Observable.just(
        listOf(
            Budget(1, "Wyjazd", "#ffffff", 1),
            Budget(2, "Kupony", "#999999", 2),
            Budget(3, "Zajazd", "#555555", 2),
            Budget(4, "Grill", "#333333", 1),
            Budget(5, "Prezenty", "#321331", 3)
        )
    )
        .replay()
        .refCount()

    private val addedObservable = addBudgetSubject
        .scan(listOf<Budget>()) { items, item ->
            listOf<Budget>()
                .plus(item)
                .plus(items.filter { it.name != item.name })
        }
        .replay()
        .refCount()

    private val deletedObservable = deleteBudgetSubject
        .scan(listOf<Budget>()) { items, item ->
            listOf<Budget>()
                .plus(item)
                .plus(items)
                .distinctBy { it.id }
        }
        .replay()
        .refCount()

    val budgetsObservable: Observable<Either<DefaultError, List<Budget>>> =
        Observables.combineLatest(storedItems, addedObservable, deletedObservable) { stored, added, deleted ->
            emptyList<Budget>()
                .plus(added)
                .plus(stored)
                .filter { !deleted.map { it.id }.contains(it.id) }
        }
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .replay()
            .refCount()

    fun getBudget(budgetId: Long): Observable<Either<DefaultError, Budget>> = budgetsObservable
        .mapRightWithEither {
            it
                .firstOrNull { it.id == budgetId }
                .toOption()
                .fold({ Either.left(NotFoundError) }, { Either.right(it) })
        }

    fun addBudgetSingle(budget: Budget): Single<Unit> = addBudgetSubject.executeFromSingle(budget)

    fun deleteBudgetSingle(budgetId: Long): Single<Unit> =
        deleteBudgetSubject.executeFromSingle(Budget(budgetId, "", "", 1))

    fun getBudgetUsersSingle(): Single<Either<DefaultError, List<BudgetUser>>> =
        Single.just(
            listOf(
                BudgetUser(1, "Marek", 1, 1),
                BudgetUser(2, "Czarek", 1, null),
                BudgetUser(3, "Jarek", 1, 3),
                BudgetUser(4, "Darek", 1, null),
                BudgetUser(5, "Arek", 1, 5)
            )
        )
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
}
