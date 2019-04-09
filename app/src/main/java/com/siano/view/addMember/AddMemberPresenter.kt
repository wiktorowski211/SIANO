package com.siano.view.addMember

import com.appunite.rx.dagger.UiScheduler
import com.siano.api.model.Member
import com.siano.dao.MemberDao
import com.siano.utils.executeFromSingle
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Named

class AddMemberPresenter @Inject constructor(
    private val memberDao: MemberDao,
    @Named("budgetId") val budgetId: Long,
    @UiScheduler val uiScheduler: Scheduler
) {
    private val memberNameSubject = BehaviorSubject.create<String>()
    private val saveMemberSubject = PublishSubject.create<Unit>()

    private val memberNameObservable: Observable<String> = memberNameSubject
        .replay()
        .refCount()

    fun canSaveObservable(): Observable<Boolean> = memberNameObservable.map { it.isNotBlank() }

    fun saveBudgetObservable(): Observable<Unit> = saveMemberSubject
        .withLatestFrom(
            memberNameObservable
        ) { _, name ->
            Member(0, name, budgetId, null)
        }
        .switchMap { memberDao.createBudgetMemberObservable(it.budget_id.toString(), it).map { Unit } }
        .observeOn(uiScheduler)

    fun setMemberNameSingle(name: String): Single<Unit> = memberNameSubject.executeFromSingle(name)

    fun saveMemberSingle(): Single<Unit> = saveMemberSubject.executeFromSingle(Unit)
}
