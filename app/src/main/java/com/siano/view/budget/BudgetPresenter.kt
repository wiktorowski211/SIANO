package com.siano.view.budget

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Budget
import com.siano.api.model.Transaction
import com.siano.dao.BudgetDao
import com.siano.dao.TransactionDao
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.mapToLeftOption
import com.siano.utils.onlyRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject
import javax.inject.Named

class BudgetPresenter @Inject constructor(
    budgetDao: BudgetDao,
    transactionDao: TransactionDao,
    @Named("budgetId") val budgetId: Long,
    @UiScheduler uiScheduler: Scheduler
) {
    private val deleteBudgetSubject = PublishSubject.create<Unit>()

    private val findBudgetObservable: Observable<Either<DefaultError, Budget>> = budgetDao.getBudget(budgetId)
        .replay()
        .refCount()

    val budgetObservable = findBudgetObservable
        .onlyRight()
        .replay()
        .refCount()

    private val transactionObservable: Observable<Either<DefaultError, List<Transaction>>> =
        transactionDao.transactionsObservable
            .observeOn(uiScheduler)

    val itemsObservable: Observable<List<BaseAdapterItem>> = transactionObservable
        .onlyRight()
        .map { transactions ->
            transactions
                .flatMap { it.shares }
                .groupBy { it.user }
                .map { BudgetAdapterItem(it.key.name, it.value.sumByDouble { shares -> shares.amount }) }
        }

    val errorObservable: Observable<Option<DefaultError>> = transactionObservable
        .mapToLeftOption()

    val deleteBudgetObservable = deleteBudgetSubject
        .switchMapSingle { budgetDao.deleteBudgetSingle(budgetId) }

    fun deleteBudgetSingle(): Single<Unit> = deleteBudgetSubject.executeFromSingle(Unit)
}
