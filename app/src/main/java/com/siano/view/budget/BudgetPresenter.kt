package com.siano.view.budget

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Budget
import com.siano.api.model.Member
import com.siano.api.model.Transaction
import com.siano.dao.AuthDao
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
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject
import javax.inject.Named

class BudgetPresenter @Inject constructor(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao,
    private val memberDao: MemberDao,
    authDao: AuthDao,
    @Named("budgetId") val budgetId: Long,
    @UiScheduler uiScheduler: Scheduler
) {
    private val deleteBudgetSubject = PublishSubject.create<Unit>()

    private val createReportSubject = PublishSubject.create<Unit>()

    private val findBudgetObservable: Observable<Either<DefaultError, Budget>> = budgetDao.getBudgetObservable(budgetId)
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    val getReportTransactionsObservable: Observable<Unit> =
        createReportSubject
            .withLatestFrom(transactionDao.getTransactionsObservable(budgetId.toString()).onlyRight()) { _, transactions ->
                BudgetReport.createReport(transactions)
                Unit
            }

    val budgetObservable = findBudgetObservable
        .onlyRight()
        .replay()
        .refCount()

    val userObservable = authDao.getUser()
        .observeOn(uiScheduler)
        .onlyRight()
        .replay()
        .refCount()

    val isUserBudgetOwnerObservable: Observable<Boolean> =
        Observables.combineLatest(userObservable, budgetObservable) { user, budget ->
            budget.owner_id == user.id
        }

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
                transactions.flatMap { transaction -> transaction.shares }
            it.map { member ->
                val amount =
                    shares.filter { share -> share.member_id == member.id }.sumByDouble { share -> share.amount }
                BudgetAdapterItem(member.nickname, amount) as BaseAdapterItem
            }
        }
    }
        .replay()
        .refCount()

    val errorObservable: Observable<Option<DefaultError>> = transactionsObservable
        .mapToLeftOption()

    private val deleteBudgetObservable = deleteBudgetSubject
        .switchMap { budgetDao.deleteBudgetSingle(budgetId) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    fun refreshBudgetObservable(): Observable<Unit> = Single.merge(
        budgetDao.refreshBudgetsSingle(),
        transactionDao.refreshTransactionsSingle(),
        memberDao.refreshBudgetMembersSingle()
    ).toObservable()

    fun deleteSuccessObservable() = deleteBudgetObservable
        .onlyRight()

    fun deleteErrorObservable() = deleteBudgetObservable
        .mapToLeftOption()

    fun deleteBudgetSingle(): Single<Unit> = deleteBudgetSubject.executeFromSingle(Unit)

    fun getReportSingle(): Single<Unit> = createReportSubject.executeFromSingle(Unit)
}

