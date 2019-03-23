package com.siano.view.main

import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import com.siano.dao.GithubDao
import com.siano.utils.DefaultError
import com.siano.utils.onlyLeft
import com.siano.utils.onlyRight
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class RepositoriesPresenter @Inject constructor(
    githubDao: GithubDao,
    @UiScheduler uiScheduler: Scheduler
) {
    private val openIssuesForRepository = PublishSubject.create<Unit>()

    private val repositoriesSingle = githubDao.getUserRepositoriesObservable()
        .observeOn(uiScheduler)

    val itemsObservable: Observable<List<BaseAdapterItem>> = repositoriesSingle
        .toObservable()
        .onlyRight()
        .map { repositories -> repositories.map { RepositoryAdapterItem(it, openIssuesForRepository) } }

    val errorObservable: Observable<DefaultError> = repositoriesSingle
        .toObservable()
        .onlyLeft()

    fun itemClick(): Observable<Unit> = openIssuesForRepository
}
