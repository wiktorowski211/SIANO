package com.siano.view.joinBudget

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.api.model.Member
import com.siano.dao.BudgetDao
import com.siano.utils.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.funktionale.either.Either
import org.funktionale.option.Option
import javax.inject.Inject

class JoinBudgetPresenter @Inject constructor(
    budgetDao: BudgetDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val codeSubject = BehaviorSubject.createDefault("")
    private val joinBudgetSubject = PublishSubject.create<Unit>()
    private val chooseMemberSubject = PublishSubject.create<Member>()

    private val codeObservable: Observable<Either<DefaultError, String>> = codeSubject.map {
        when {
            it.isBlank() -> Either.left(EmptyInputError)
            else -> Either.right(it)
        }
    }

    private val membersObservable: Observable<Either<DefaultError, List<Member>>> = joinBudgetSubject
        .withLatestFrom(codeObservable) { _, code ->
            when {
                code.isLeft() -> Either.left(EmptyInputError as DefaultError)
                else -> code
            }
        }
        .switchMapRightWithEither { budgetDao.getJoinBudgetMembers(it) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    private val joinBudgetObservable = chooseMemberSubject
        .withLatestFrom(codeObservable.onlyRight()) { member, code -> Pair(code, member) }
        .switchMap { budgetDao.joinBudget(it.first, it.second) }
        .observeOn(uiScheduler)
        .replay()
        .refCount()

    val itemsObservable: Observable<List<BaseAdapterItem>> = membersObservable
        .mapToRightOr(listOf())
        .map { budgets -> budgets.map { JoinBudgetAdapterItem(it, chooseMemberSubject) } }

    val showInfoTextObservable: Observable<Boolean> = membersObservable
        .map {
            when{
                it.isLeft() -> false
                else -> it.right().get().isNotEmpty()
            }
        }

    val joinBudgetSuccess = joinBudgetObservable.onlyRight()

    val errorObservable: Observable<Option<DefaultError>> =
        Observable.merge(membersObservable.mapToLeftOption(), joinBudgetObservable.mapToLeftOption())

    fun incorrectCodeObservable(): Observable<Option<DefaultError>> = codeObservable
        .skip(1)
        .mapToLeftOption()

    fun codeSingle(code: String): Single<Unit> = codeSubject.executeFromSingle(code)

    fun joinBudgetSingle(): Single<Unit> = joinBudgetSubject.executeFromSingle(Unit)
}
