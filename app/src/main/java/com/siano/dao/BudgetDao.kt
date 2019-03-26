package com.siano.dao

import com.appunite.rx.dagger.NetworkScheduler
import com.siano.api.ApiService
import com.siano.api.model.Budget
import com.siano.api.model.BudgetUser
import com.siano.utils.DefaultError
import com.siano.utils.handleEitherRestErrors
import io.reactivex.Scheduler
import io.reactivex.Single
import org.funktionale.either.Either
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetDao @Inject constructor(
    private val apiService: ApiService,
    @NetworkScheduler private val networkScheduler: Scheduler
) {
    fun getBudgetsSingle(): Single<Either<DefaultError, List<Budget>>> =
        Single.just(
            listOf(
                Budget(1, "Wyjazd", "#eee9e9", 1),
                Budget(2, "Kupony", "#dcccdc", 2),
                Budget(3, "Zajazd", "#bdb76b", 2),
                Budget(4, "Grill", "#00fa9a", 1),
                Budget(5, "Prezenty", "#3cb371", 3)
            )
        )
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()

    fun getBudgetUsersSingle(): Single<Either<DefaultError, List<BudgetUser>>> =
        Single.just(
            listOf(
                BudgetUser(1, "Marek", 1, 1),
                BudgetUser(2, "Czarek", 1, null),
                BudgetUser(3, "Jarek", 1, 3),
                BudgetUser(4, "Darek", 1, null),
                BudgetUser(5, "Arek", 1, 5)
            )
        )
            .subscribeOn(networkScheduler)
            .handleEitherRestErrors()
}
