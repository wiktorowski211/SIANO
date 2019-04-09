package com.siano.view.transaction

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Member
import com.siano.api.model.ForWhat
import com.siano.api.model.Transaction
import com.siano.api.model.TransactionShare
import com.siano.dagger.annotations.Scope
import com.siano.dao.MemberDao
import com.siano.dao.TransactionDao
import com.siano.utils.DefaultError
import com.siano.utils.executeFromSingle
import com.siano.utils.mapToLeftOption
import com.siano.utils.onlyRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject
import javax.inject.Named

@Scope.Activity
class TransactionPresenter @Inject constructor(
    transactionDao: TransactionDao,
    memberDao: MemberDao,
    @Named("budgetId") budgetId: Long,
    @UiScheduler uiScheduler: Scheduler
) {
    private val fromWhomSubject = PublishSubject.create<TransactionShare>()
    private val categorySubject = PublishSubject.create<Int>()
    private val toWhomSubject = PublishSubject.create<TransactionShare>()
    private val titleSubject = PublishSubject.create<String>()

    private val saveTransactionSubject = PublishSubject.create<Unit>()
    private val selectedPageChangedSubject = BehaviorSubject.createDefault(0)

    private val membersObservable: Observable<Either<DefaultError, List<Member>>> =
        memberDao.getBudgetMembersObservable(budgetId.toString())
            .observeOn(uiScheduler)
            .replay()
            .refCount()

    val fromWhomItemsObservable: Observable<List<BaseAdapterItem>> = membersObservable
        .onlyRight()
        .map { members -> members.map { TransactionAdapterItem(it, fromWhomSubject) } }

    val toWhomItemsObservable: Observable<List<BaseAdapterItem>> = membersObservable
        .onlyRight()
        .map { members -> members.map { TransactionAdapterItem(it, toWhomSubject) } }

    private val fromWhomSelectedItemsObservable: Observable<List<TransactionShare>> = fromWhomSubject
        .scan(listOf<TransactionShare>()) { items, item ->
            listOf<TransactionShare>()
                .plus(items.filter { it.member_id != item.member_id })
                .plus(item)
                .filter { it.amount != 0.0 }
        }
        .replay()
        .refCount()

    private val toWhomSelectedItemsObservable: Observable<List<TransactionShare>> = toWhomSubject
        .scan(listOf<TransactionShare>()) { items, item ->
            listOf<TransactionShare>()
                .plus(items.filter { it.member_id != item.member_id })
                .plus(TransactionShare(-item.amount, item.member_id))
                .filter { it.amount != 0.0 }
        }
        .replay()
        .refCount()

    private val titleObservable: Observable<String> = titleSubject

    private val categoryObservable: Observable<Int> = categorySubject
        .startWith(0)


    private val forWhatObservable: Observable<ForWhat> =
        Observables.combineLatest(titleObservable, categoryObservable) { title, category -> ForWhat(title, category) }
            .replay()
            .refCount()

    private val createTransactionResponseObservable: Observable<Either<DefaultError, Unit>> = saveTransactionSubject
        .withLatestFrom(
            fromWhomSelectedItemsObservable,
            forWhatObservable,
            toWhomSelectedItemsObservable
        ) { _, fromWhom, forWhat, toWhom ->
            val shares = emptyList<TransactionShare>()
                .plus(fromWhom)
                .plus(toWhom)

            Transaction(budgetId, forWhat.title, forWhat.category, shares)
        }
        .switchMap {
            transactionDao.createTransactionObservable(budgetId.toString(), it).observeOn(uiScheduler)
        }
        .replay()
        .refCount()

    val totalAmount: Observable<Double> = fromWhomSelectedItemsObservable
        .map { shares -> shares.sumByDouble { it.amount } }
        .replay()
        .refCount()

    val remainingAmountObservable: Observable<Double> = Observables.combineLatest(
        fromWhomSelectedItemsObservable,
        toWhomSelectedItemsObservable
    ) { fromWhom, toWhom ->
        emptyList<Double>()
            .plus(fromWhom.map { it.amount })
            .plus(toWhom.map { it.amount })
            .sumByDouble { it }
    }
        .replay()
        .refCount()

    val showSaveButtonObservable: Observable<Boolean> = Observables.combineLatest(
        fromWhomSelectedItemsObservable.map { it.isNotEmpty() },
        forWhatObservable.map { it.title.isNotBlank() },
        fromWhomSelectedItemsObservable.map { it.isNotEmpty() },
        remainingAmountObservable.map { it == 0.0 })
    { fromWhom, forWhat, toWhom, remainingAmount -> fromWhom && forWhat && toWhom && remainingAmount }
        .startWith(false)
        .replay()
        .refCount()

    val showNextButtonObservable: Observable<Boolean> = selectedPageChangedSubject
        .switchMap { page ->
            val p = TransactionActivity.TransactionPagerAdapter

            when (page) {
                p.FROM_WHOM -> fromWhomSelectedItemsObservable.map { it.isNotEmpty() }
                p.FOR_WHAT -> forWhatObservable.map { it.title.isNotBlank() }
                p.TO_WHOM -> Observables.combineLatest(toWhomSelectedItemsObservable, remainingAmountObservable)
                { toWhom, remainingAmount ->
                    toWhom.isNotEmpty() && remainingAmount == 0.0
                }
                else -> Observable.just(false)
            }
        }
        .withLatestFrom(showSaveButtonObservable) { showNextButton, showSaveButton -> showNextButton && !showSaveButton }
        .replay()
        .refCount()

    fun successObservable(): Observable<Unit> = createTransactionResponseObservable
        .onlyRight()

    fun errorObservable(): Observable<Option<DefaultError>> = createTransactionResponseObservable
        .mapToLeftOption()

    fun saveTransactionSingle() = saveTransactionSubject.executeFromSingle(Unit)

    fun selectedPageChangedSingle(position: Int) = selectedPageChangedSubject.executeFromSingle(position)

    fun titleChangedSingle(title: String) = titleSubject.executeFromSingle(title)

    fun categoryChangedSingle(position: Int) = categorySubject.executeFromSingle(position)
}
