package com.siano.api

import com.siano.utils.DefaultError
import com.siano.utils.EmptyError
import com.siano.utils.UnknownServerError
import com.siano.utils.mapRightWithEither
import io.reactivex.Observable
import io.reactivex.Single
import org.funktionale.either.Either

data class Response<T>(
    val data: T? = null,
    val error: String? = null
)

fun <T1> Observable<Either<DefaultError, Response<T1>>>.extractResponse(): Observable<Either<DefaultError, T1>> =
    mapRightWithEither {
        when{
            it.error != null -> Either.left(UnknownServerError(it.error))
            it.data == null -> Either.left(EmptyError)
            else -> Either.right(it.data)
        }
    }

fun <T1> Single<Either<DefaultError, Response<T1>>>.extractResponse(): Single<Either<DefaultError, T1>> =
    mapRightWithEither {
        when{
            it.error != null -> Either.left(UnknownServerError(it.error))
            it.data == null -> Either.left(EmptyError)
            else -> Either.right(it.data)
        }
    }