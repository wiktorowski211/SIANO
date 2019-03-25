package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.Transaction
import com.siano.utils.DefaultError
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Scheduler
import io.reactivex.Single
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    fun createTransaction(transaction:Transaction): Single<Either<DefaultError, Unit>> =
        Single.just(Unit)
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
}
