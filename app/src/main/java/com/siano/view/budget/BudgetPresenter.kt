package com.siano.view.budget

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Budget
import com.siano.api.model.Member
import com.siano.api.model.Transaction
import com.siano.dao.BudgetDao
import com.siano.dao.MemberDao
import com.siano.dao.TransactionDao
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.mapToLeftOption
import com.siano.utils.onlyRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject
import javax.inject.Named

class BudgetPresenter @Inject constructor(
    budgetDao: BudgetDao,
    transactionDao: TransactionDao,
    memberDao: MemberDao,
    @Named("budgetId") val budgetId: Long,
    @UiScheduler uiScheduler: Scheduler
) {
    private val deleteBudgetSubject = PublishSubject.create<Unit>()

    private val findBudgetObservable: Observable<Either<DefaultError, Budget>> = budgetDao.getBudgetObservable(budgetId)
        .replay()
        .refCount()

    val budgetObservable = findBudgetObservable
        .onlyRight()
        .replay()
        .refCount()

    private val transactionsObservable: Observable<Either<DefaultError, List<Transaction>>> =
        transactionDao.getTransactionsObservable(budgetId.toString())
            .observeOn(uiScheduler)

    private val membersObservable: Observable<Either<DefaultError, List<Member>>> =
        memberDao.getBudgetMembersObservable(budgetId.toString())
            .observeOn(uiScheduler)
            .replay()
            .refCount()

    val itemsObservable: Observable<List<BaseAdapterItem>> = Observables.combineLatest(
        membersObservable.onlyRight(),
        transactionsObservable.onlyRight()
    ) { members, transactions ->
        members.let {
            val shares =
                transactions.flatMap { transaction -> transaction.shares.orEmpty()}
            it.map { member ->
                val amount =
                    shares.filter { share -> share.member_id == member.id }.sumByDouble { share -> share.amount }
                BudgetAdapterItem(member.nickname, amount)
            }
        }
    }

    val errorObservable: Observable<Option<DefaultError>> = transactionsObservable
        .mapToLeftOption()

    private val deleteBudgetObservable = deleteBudgetSubject
        .switchMap { budgetDao.deleteBudgetSingle(budgetId) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    fun deleteSuccessObservable() = deleteBudgetObservable
        .onlyRight()

    fun deleteErrorObservable() = deleteBudgetObservable
        .mapToLeftOption()

    fun deleteBudgetSingle(): Single<Unit> = deleteBudgetSubject.executeFromSingle(Unit)
}
