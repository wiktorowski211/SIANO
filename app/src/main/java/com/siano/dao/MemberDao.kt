package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.extractResponse
import com.siano.api.model.Member
import com.siano.api.model.MemberRequest
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
class MemberDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    private val refreshBudgetMembersSubject: BehaviorSubject<Unit> = BehaviorSubject.createDefault(Unit)

    fun getBudgetMembersObservable(budgetId: String): Observable<Either<DefaultError, List<Member>>> =
        refreshBudgetMembersSubject
            .switchMapSingle { apiService.getBudgetMembers(budgetId).subscribeOn(networkScheduler) }
            .handleEitherRestErrors()
            .extractResponse()
            .replay()
            .refCount()

    fun createBudgetMemberObservable(
        budgetId: String,
        member: Member
    ): Observable<Either<DefaultError, Unit>> =
        apiService.createBudgetMember(budgetId, MemberRequest(member))
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
            .toObservable()
            .switchMapRight { refreshBudgetMembersSubject.executeFromSingle(Unit).toObservable() }

    fun refreshBudgetMembersSingle(): Single<Unit> = refreshBudgetMembersSubject.executeFromSingle(Unit)
}
