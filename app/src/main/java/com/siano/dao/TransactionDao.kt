package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.BudgetUser
import com.siano.api.model.Transaction
import com.siano.api.model.TransactionShare
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.ReplaySubject
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val addTransactionSubject: ReplaySubject<Transaction> = ReplaySubject.create()

    private val storedItems: Observable<List<Transaction>> = Observable.just(
        listOf(
            Transaction(
                1,
                "Frytki", "Jedzenie", listOf(
                    TransactionShare(50.0, BudgetUser(1, "Marek", 1, 1)),
                    TransactionShare(50.0, BudgetUser(2, "Czarek", 1, null)),
                    TransactionShare(-25.0, BudgetUser(1, "Marek", 1, 1)),
                    TransactionShare(-25.0, BudgetUser(2, "Czarek", 1, null)),
                    TransactionShare(-25.0, BudgetUser(3, "Jarek", 1, 3)),
                    TransactionShare(-25.0, BudgetUser(4, "Darek", 1, null))
                )
            ),
            Transaction(
                1,
                "Zakupy", "Zakupy", listOf(
                    TransactionShare(10.0, BudgetUser(1, "Marek", 1, 1)),
                    TransactionShare(-5.0, BudgetUser(1, "Marek", 1, 1)),
                    TransactionShare(-5.0, BudgetUser(2, "Czarek", 1, null)),
                    TransactionShare(-5.0, BudgetUser(3, "Jarek", 1, 3))
                )
            ),
            Transaction(
                1,
                "Zwrot", "Przelew", listOf(
                    TransactionShare(30.0, BudgetUser(3, "Jarek", 1, 3)),
                    TransactionShare(-30.0, BudgetUser(1, "Marek", 1, 1))
                )
            )
        )
    )
        .replay()
        .refCount()

    private val addedObservable = addTransactionSubject
        .scan(listOf<Transaction>()) { items, item ->
            listOf<Transaction>()
                .plus(item)
                .plus(items)
        }
        .replay()
        .refCount()

    val transactionsObservable: Observable<Either<DefaultError, List<Transaction>>> =
        Observables.combineLatest(storedItems, addedObservable) { stored, added ->
            emptyList<Transaction>()
                .plus(added)
                .plus(stored)
        }
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .replay()
            .refCount()

    fun addTransactionSingle(transaction: Transaction): Single<Either<DefaultError, Unit>> = addTransactionSubject
        .executeFromSingle(transaction)
        .handleEitherRestErrors()
}
