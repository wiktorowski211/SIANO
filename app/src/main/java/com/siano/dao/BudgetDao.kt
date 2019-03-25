package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.Budget
import com.siano.api.model.BudgetUser
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val addBudgetSubject: BehaviorSubject<Budget> = BehaviorSubject.createDefault(
        Budget(1, "Wyjazd", "#ffffff", 1)
    )

    private val storedItems: Observable<List<Budget>> = Observable.just(
        listOf(
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

    fun getBudgetsSingle() = Observables.combineLatest(storedItems, addedObservable) { stored, added ->
        emptyList<Budget>()
            .plus(added)
            .plus(stored)
    }
        .subscribeOn(networkScheduler)
        .handleEitherRestErrors()

    fun addBudgetSingle(budget: Budget): Single<Unit> = addBudgetSubject.executeFromSingle(budget)

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
