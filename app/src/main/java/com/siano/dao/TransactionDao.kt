package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.extractResponse
import com.siano.api.model.Transaction
import com.siano.api.model.TransactionRequest
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.handleEitherRestErrors
import com.siano.utils.switchMapRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val refreshTransactionsSubject: BehaviorSubject<Unit> = BehaviorSubject.createDefault(Unit)

    fun getTransactionsObservable(budgetId: String): Observable<Either<DefaultError, List<Transaction>>> =
        refreshTransactionsSubject
            .switchMapSingle { apiService.getBudgetTransactions(budgetId).subscribeOn(networkScheduler) }
            .handleEitherRestErrors()
            .extractResponse()
            .replay()
            .refCount()

    fun createTransactionObservable(
        budgetId: String,
        transaction: Transaction
    ): Observable<Either<DefaultError, Unit>> =
        apiService.createBudgetTransaction(budgetId, TransactionRequest(transaction))
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .toObservable()
            .switchMapRight { refreshTransactionsSubject.executeFromSingle(Unit).toObservable() }

    fun refreshTransactionsSingle(): Single<Unit> = refreshTransactionsSubject.executeFromSingle(Unit)
}
