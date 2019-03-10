package com.siano.view.main

import com.siano.dao.GithubDao
import com.appunite.rx.ResponseOrError
import com.appunite.rx.dagger.UiScheduler
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import rx.Scheduler
import rx.subjects.PublishSubject
import javax.inject.Inject

class RepositoriesPresenter @Inject constructor(
        githubDao: GithubDao,
        @UiScheduler uiScheduler: Scheduler) {
    private val openIssuesForRepository = PublishSubject.create<Any>()

    private val repositoriesObservable = githubDao.getUserRepositoriesObservable()
            .observeOn(uiScheduler)
            .replay(1)
            .refCount()

    val itemsObservable = repositoriesObservable
            .compose(ResponseOrError.onlySuccess())
            .map { repositories -> repositories.mapTo(mutableListOf<BaseAdapterItem>()) { RepositoryAdapterItem(it, openIssuesForRepository) } }

    val errorObservable = repositoriesObservable
            .compose(ResponseOrError.onlyError())
}
