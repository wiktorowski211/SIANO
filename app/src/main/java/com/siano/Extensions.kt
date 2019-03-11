package com.siano

import com.siano.utils.*
import io.reactivex.*
import org.funktionale.either.Either
import org.funktionale.either.flatMap
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import org.funktionale.tries.Try
import java.io.IOException

// RxEither

// switchMap

fun <L, R, TL> Observable<Either<L, R>>.switchMapLeftWithEither(function: (L) -> Observable<Either<TL, R>>): Observable<Either<TL, R>> =
    this.switchMap { it.fold<Observable<Either<TL, R>>>(function, { r -> Observable.just(Either.right(r)) }) }

fun <L, R, TR> Observable<Either<L, R>>.switchMapRightWithEither(function: (R) -> Observable<Either<L, TR>>): Observable<Either<L, TR>> =
    this.switchMap { it.fold<Observable<Either<L, TR>>>({ l -> Observable.just(Either.left(l)) }, function) }

fun <L, R, TL> Observable<Either<L, R>>.switchMapLeft(function: (L) -> Observable<TL>): Observable<Either<TL, R>> =
    this.switchMap {
        it.fold<Observable<Either<TL, R>>>({ l -> function(l).map { tl -> Either.left(tl) } }, {
            Observable.just(
                Either.right(it)
            )
        })
    }

fun <L, R, TR> Observable<Either<L, R>>.switchMapRight(function: (R) -> Observable<TR>): Observable<Either<L, TR>> =
    this.switchMap {
        it.fold<Observable<Either<L, TR>>>(
            { Observable.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }

fun <L, R, TL> Flowable<Either<L, R>>.switchMapLeftWithEither(function: (L) -> Flowable<Either<TL, R>>): Flowable<Either<TL, R>> =
    this.switchMap { it.fold<Flowable<Either<TL, R>>>(function, { Flowable.just(Either.right(it)) }) }

fun <L, R, TR> Flowable<Either<L, R>>.switchMapRightWithEither(function: (R) -> Flowable<Either<L, TR>>): Flowable<Either<L, TR>> =
    this.switchMap { it.fold<Flowable<Either<L, TR>>>({ Flowable.just(Either.left(it)) }, function) }

fun <L, R, TL> Flowable<Either<L, R>>.switchMapLeft(function: (L) -> Flowable<TL>): Flowable<Either<TL, R>> =
    this.switchMap {
        it.fold<Flowable<Either<TL, R>>>({ function(it).map { Either.left(it) } }, {
            Flowable.just(
                Either.right(it)
            )
        })
    }

fun <L, R, TR> Flowable<Either<L, R>>.switchMapRight(function: (R) -> Flowable<TR>): Flowable<Either<L, TR>> =
    this.switchMap {
        it.fold<Flowable<Either<L, TR>>>(
            { Flowable.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }

// flatMap

fun <L, R, TL> Observable<Either<L, R>>.flatMapLeftWithEither(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (L) -> Observable<Either<TL, R>>
): Observable<Either<TL, R>> =
    this.flatMap(
        { it.fold<Observable<Either<TL, R>>>(function, { Observable.just(Either.right(it)) }) },
        maxConcurrency
    )

fun <L, R, TR> Observable<Either<L, R>>.flatMapRightWithEither(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (R) -> Observable<Either<L, TR>>
): Observable<Either<L, TR>> =
    this.flatMap({ it.fold<Observable<Either<L, TR>>>({ Observable.just(Either.left(it)) }, function) }, maxConcurrency)

fun <L, R, TL> Observable<Either<L, R>>.flatMapLeft(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (L) -> Observable<TL>
): Observable<Either<TL, R>> =
    this.flatMap({
        it.fold<Observable<Either<TL, R>>>({ function(it).map { Either.left(it) } }, {
            Observable.just(
                Either.right(it)
            )
        })
    }, maxConcurrency)

fun <L, R, TR> Observable<Either<L, R>>.flatMapRight(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (R) -> Observable<TR>
): Observable<Either<L, TR>> =
    this.flatMap({
        it.fold<Observable<Either<L, TR>>>(
            { Observable.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }, maxConcurrency)

fun <L, R, TL> Flowable<Either<L, R>>.flatMapLeftWithEither(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (L) -> Flowable<Either<TL, R>>
): Flowable<Either<TL, R>> =
    this.flatMap({ it.fold<Flowable<Either<TL, R>>>(function, { Flowable.just(Either.right(it)) }) }, maxConcurrency)

fun <L, R, TL> Flowable<Either<L, R>>.flatMapLeftWithEitherSingle(function: (L) -> Single<Either<TL, R>>): Flowable<Either<TL, R>> =
    this.flatMapSingle { it.fold<Single<Either<TL, R>>>(function) { Single.just(Either.right(it)) } }

fun <L, R, TR> Flowable<Either<L, R>>.flatMapRightWithEither(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (R) -> Flowable<Either<L, TR>>
): Flowable<Either<L, TR>> =
    this.flatMap({ it.fold<Flowable<Either<L, TR>>>({ Flowable.just(Either.left(it)) }, function) }, maxConcurrency)

fun <L, R, TR> Flowable<Either<L, R>>.flatMapRightWithEitherSingle(function: (R) -> Single<Either<L, TR>>): Flowable<Either<L, TR>> =
    this.flatMapSingle { it.fold<Single<Either<L, TR>>>({ Single.just(Either.left(it)) }, function) }

fun <L, R, TL> Flowable<Either<L, R>>.flatMapLeft(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (L) -> Flowable<TL>
): Flowable<Either<TL, R>> =
    this.flatMap({
        it.fold<Flowable<Either<TL, R>>>(
            { function(it).map { Either.left(it) } },
            { Flowable.just(Either.right(it)) })
    }, maxConcurrency)

fun <L, R, TR> Flowable<Either<L, R>>.flatMapRight(
    maxConcurrency: Int = kotlin.Int.MAX_VALUE,
    function: (R) -> Flowable<TR>
): Flowable<Either<L, TR>> =
    this.flatMap({
        it.fold<Flowable<Either<L, TR>>>(
            { Flowable.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }, maxConcurrency)

fun <L, R, TL> Single<Either<L, R>>.flatMapLeftWithEither(function: (L) -> Single<Either<TL, R>>): Single<Either<TL, R>> =
    this.flatMap { it.fold<Single<Either<TL, R>>>(function, { Single.just(Either.right(it)) }) }

fun <L, R, TR> Single<Either<L, R>>.flatMapRightWithEither(function: (R) -> Single<Either<L, TR>>): Single<Either<L, TR>> =
    this.flatMap { it.fold<Single<Either<L, TR>>>({ Single.just(Either.left(it)) }, function) }

fun <L, R, TL> Single<Either<L, R>>.flatMapLeft(function: (L) -> Single<TL>): Single<Either<TL, R>> =
    this.flatMap {
        it.fold<Single<Either<TL, R>>>(
            { function(it).map { Either.left(it) } },
            { Single.just(Either.right(it)) })
    }

fun <L, R, TR> Single<Either<L, R>>.flatMapRight(function: (R) -> Single<TR>): Single<Either<L, TR>> =
    this.flatMap {
        it.fold<Single<Either<L, TR>>>(
            { Single.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }

fun <T, R> Single<Option<T>>.flatMapDefined(function: (T) -> Single<R>): Single<Option<R>> =
    this.flatMap { it.fold({ Single.just(Option.None) }, { function(it).map { Option.Some(it) } }) }

fun <T, R> Single<Option<T>>.flatMapDefinedWithOption(function: (T) -> Single<Option<R>>): Single<Option<R>> =
    this.flatMap { it.fold({ Single.just(Option.None) }, { function(it) }) }

fun <T, R> Observable<Option<T>>.flatMapDefined(function: (T) -> Observable<R>): Observable<Option<R>> =
    this.flatMap { it.fold({ Observable.just(Option.None) }, { function(it).map { Option.Some(it) } }) }

fun <T, R> Observable<Option<T>>.flatMapDefinedWithOption(function: (T) -> Observable<Option<R>>): Observable<Option<R>> =
    this.flatMap { it.fold({ Observable.just(Option.None) }, { function(it) }) }

fun <T, R> Flowable<Option<T>>.flatMapDefined(function: (T) -> Flowable<R>): Flowable<Option<R>> =
    this.flatMap { it.fold({ Flowable.just(Option.None) }, { function(it).map { Option.Some(it) } }) }

fun <T, R> Flowable<Option<T>>.flatMapDefinedWithOption(function: (T) -> Flowable<Option<R>>): Flowable<Option<R>> =
    this.flatMap { it.fold({ Flowable.just(Option.None) }, { function(it) }) }

// flatMapSingle

fun <L, R, TL> Observable<Either<L, R>>.flatMapSingleLeftWithEither(
    delayErrors: Boolean = false,
    function: (L) -> Single<Either<TL, R>>
): Observable<Either<TL, R>> =
    this.flatMapSingle({ it.fold(function, { Single.just(Either.right(it)) }) }, delayErrors)

fun <L, R, TR> Observable<Either<L, R>>.flatMapSingleRightWithEither(
    delayErrors: Boolean = false,
    function: (R) -> Single<Either<L, TR>>
): Observable<Either<L, TR>> =
    this.flatMapSingle({ it.fold({ Single.just(Either.left(it)) }, function) }, delayErrors)

fun <L, R, TL> Observable<Either<L, R>>.flatMapSingleLeft(
    delayErrors: Boolean = false,
    function: (L) -> Single<TL>
): Observable<Either<TL, R>> =
    this.flatMapSingle(
        { it.fold({ function(it).map { Either.left(it) } }, { Single.just(Either.right(it)) }) },
        delayErrors
    )

fun <L, R, TR> Observable<Either<L, R>>.flatMapSingleRight(
    delayErrors: Boolean = false,
    function: (R) -> Single<TR>
): Observable<Either<L, TR>> =
    this.flatMapSingle(
        { it.fold({ Single.just(Either.left(it)) }, { function(it).map { Either.right(it) } }) },
        delayErrors
    )

// concatMap

fun <L, R, TL> Observable<Either<L, R>>.concatMapLeftWithEither(function: (L) -> Observable<Either<TL, R>>): Observable<Either<TL, R>> =
    this.concatMap { it.fold<Observable<Either<TL, R>>>(function, { Observable.just(Either.right(it)) }) }

fun <L, R, TR> Observable<Either<L, R>>.concatMapRightWithEither(function: (R) -> Observable<Either<L, TR>>): Observable<Either<L, TR>> =
    this.concatMap { it.fold<Observable<Either<L, TR>>>({ Observable.just(Either.left(it)) }, function) }

fun <L, R, TL> Observable<Either<L, R>>.concatMapLeft(function: (L) -> Observable<TL>): Observable<Either<TL, R>> =
    this.concatMap {
        it.fold<Observable<Either<TL, R>>>({ function(it).map { Either.left(it) } }, {
            Observable.just(
                Either.right(it)
            )
        })
    }

fun <L, R, TR> Observable<Either<L, R>>.concatMapRight(function: (R) -> Observable<TR>): Observable<Either<L, TR>> =
    this.concatMap {
        it.fold<Observable<Either<L, TR>>>(
            { Observable.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }

fun <L, R, TL> Flowable<Either<L, R>>.concatMapLeftWithEither(function: (L) -> Flowable<Either<TL, R>>): Flowable<Either<TL, R>> =
    this.concatMap { it.fold<Flowable<Either<TL, R>>>(function, { Flowable.just(Either.right(it)) }) }

fun <L, R, TR> Flowable<Either<L, R>>.concatMapRightWithEither(function: (R) -> Flowable<Either<L, TR>>): Flowable<Either<L, TR>> =
    this.concatMap { it.fold<Flowable<Either<L, TR>>>({ Flowable.just(Either.left(it)) }, function) }

fun <L, R, TL> Flowable<Either<L, R>>.concatMapLeft(function: (L) -> Flowable<TL>): Flowable<Either<TL, R>> =
    this.concatMap {
        it.fold<Flowable<Either<TL, R>>>({ function(it).map { Either.left(it) } }, {
            Flowable.just(
                Either.right(it)
            )
        })
    }

fun <L, R, TR> Flowable<Either<L, R>>.concatMapRight(function: (R) -> Flowable<TR>): Flowable<Either<L, TR>> =
    this.concatMap {
        it.fold<Flowable<Either<L, TR>>>(
            { Flowable.just(Either.left(it)) },
            { function(it).map { Either.right(it) } })
    }

// map

fun <L, R, TL> Observable<Either<L, R>>.mapLeft(function: (L) -> TL): Observable<Either<TL, R>> =
    this.map { it.left().map(function) }

fun <L, R, TR> Observable<Either<L, R>>.mapRight(function: (R) -> TR): Observable<Either<L, TR>> =
    this.map { it.right().map(function) }

fun <L, R, TL> Single<Either<L, R>>.mapLeft(function: (L) -> TL): Single<Either<TL, R>> =
    this.map { it.left().map(function) }

fun <L, R, TR> Single<Either<L, R>>.mapRight(function: (R) -> TR): Single<Either<L, TR>> =
    this.map { it.right().map(function) }

fun <L, R, TL> Flowable<Either<L, R>>.mapLeft(function: (L) -> TL): Flowable<Either<TL, R>> =
    this.map { it.left().map(function) }

fun <L, R, TR> Flowable<Either<L, R>>.mapRight(function: (R) -> TR): Flowable<Either<L, TR>> =
    this.map { it.right().map(function) }

// mapRightWithEither

fun <L, R, TL> Observable<Either<L, R>>.mapLeftWithEither(function: (L) -> Either<TL, R>): Observable<Either<TL, R>> =
    this.map { it.left().flatMap(function) }

fun <L, R, TR> Observable<Either<L, R>>.mapRightWithEither(function: (R) -> Either<L, TR>): Observable<Either<L, TR>> =
    this.map { it.right().flatMap(function) }

fun <L, R, TL> Single<Either<L, R>>.mapLeftWithEither(function: (L) -> Either<TL, R>): Single<Either<TL, R>> =
    this.map { it.left().flatMap(function) }

fun <L, R, TR> Single<Either<L, R>>.mapRightWithEither(function: (R) -> Either<L, TR>): Single<Either<L, TR>> =
    this.map { it.right().flatMap(function) }

fun <L, R, TL> Flowable<Either<L, R>>.mapLeftWithEither(function: (L) -> Either<TL, R>): Flowable<Either<TL, R>> =
    this.map { it.left().flatMap(function) }

fun <L, R, TR> Flowable<Either<L, R>>.mapRightWithEither(function: (R) -> Either<L, TR>): Flowable<Either<L, TR>> =
    this.map { it.right().flatMap(function) }

fun <T, R> Observable<Option<T>>.mapDefinedWithOption(function: (T) -> Option<R>): Observable<Option<R>> =
    this.map { it.flatMap(function) }

fun <T, R> Single<Option<T>>.mapDefinedWithOption(function: (T) -> Option<R>): Single<Option<R>> =
    this.map { it.flatMap(function) }

fun <L, R, TR> Observable<Either<L, R>>.mapRightOr(function: (R) -> TR, defaultValue: TR): Observable<TR> =
    this.map { it.fold({ defaultValue }, { function(it) }) }

fun <T, R> Flowable<Option<T>>.mapDefinedWithOption(function: (T) -> Option<R>): Flowable<Option<R>> =
    this.map { it.flatMap(function) }


// toOption

fun <L, R> Observable<Either<L, R>>.rightOption(): Observable<Option<R>> = map { it.right().toOption() }
fun <L, R> Observable<Either<L, R>>.leftOption(): Observable<Option<L>> = map { it.left().toOption() }
fun <L, R> Flowable<Either<L, R>>.rightOption(): Flowable<Option<R>> = map { it.right().toOption() }
fun <L, R> Flowable<Either<L, R>>.leftOption(): Flowable<Option<L>> = map { it.left().toOption() }
fun <L, R> Single<Either<L, R>>.rightOption(): Single<Option<R>> = map { it.right().toOption() }
fun <L, R> Single<Either<L, R>>.leftOption(): Single<Option<L>> = map { it.left().toOption() }

// filters
fun <L, R> Observable<Either<L, R>>.onlyLeft(): Observable<L> =
    this.concatMap { it.fold({ Observable.just(it) }, { Observable.empty() }) }

fun <L, R> Observable<Either<L, R>>.onlyRight(): Observable<R> =
    this.concatMap { it.fold({ Observable.empty<R>() }, { Observable.just(it) }) }

fun <L, R> Flowable<Either<L, R>>.onlyLeft(): Flowable<L> =
    this.concatMap { it.fold({ Flowable.just(it) }, { Flowable.empty() }) }

fun <L, R> Flowable<Either<L, R>>.onlyRight(): Flowable<R> =
    this.concatMap { it.fold({ Flowable.empty<R>() }, { Flowable.just(it) }) }

fun <T> Flowable<Option<T>>.onlyDefined(): Flowable<T> =
    this.concatMap { it.fold({ Flowable.empty<T>() }, { Flowable.just(it) }) }

// Option
fun <T> Observable<Option<T>>.onlyDefined(): Observable<T> =
    this.concatMap { it.fold({ Observable.empty<T>() }, { Observable.just(it) }) }

// map

fun <L, R> Observable<Either<L, R>>.mapToLeftOption(): Observable<Option<L>> = this.map { it.left().toOption() }

fun <L, R> Observable<Either<L, R>>.mapToRightOption(): Observable<Option<R>> = this.map { it.right().toOption() }

fun <L, R> Observable<Either<L, R>>.mapToLeftOr(value: L): Observable<L> = mapToLeftOption().mapOr(value)

fun <L, R> Observable<Either<L, R>>.mapToRightOr(value: R): Observable<R> = mapToRightOption().mapOr(value)

fun <L, R> Flowable<Either<L, R>>.mapToLeftOption(): Flowable<Option<L>> = this.map { it.left().toOption() }

fun <L, R> Flowable<Either<L, R>>.mapToRightOption(): Flowable<Option<R>> = this.map { it.right().toOption() }

fun <L, R> Flowable<Either<L, R>>.mapToLeftOr(value: L): Flowable<L> = mapToLeftOption().mapOr(value)

fun <L, R> Flowable<Either<L, R>>.mapToRightOr(value: R): Flowable<R> = mapToRightOption().mapOr(value)

fun <L, R> Single<Either<L, R>>.mapToLeftOption(): Single<Option<L>> = this.map { it.left().toOption() }

fun <L, R> Single<Either<L, R>>.mapToRightOption(): Single<Option<R>> = this.map { it.right().toOption() }

fun <L, R> Single<Either<L, R>>.mapToLeftOr(value: L): Single<L> = this.mapToLeftOption().mapOr(value)

fun <L, R> Single<Either<L, R>>.mapToRightOr(value: R): Single<R> = this.mapToRightOption().mapOr(value)

fun <T> Single<Option<T>>.mapOr(value: T): Single<T> = this.map { it.getOrElse { value } }

fun <T> Observable<Option<T>>.mapOr(value: T): Observable<T> = this.map { it.getOrElse { value } }

fun <T> Flowable<Option<T>>.mapOr(value: T): Flowable<T> = this.map { it.getOrElse { value } }


fun <T, R> Observable<Option<T>>.mapDefined(function: (T) -> R): Observable<Option<R>> = map { it.map(function) }
fun <T, R> Flowable<Option<T>>.mapDefined(function: (T) -> R): Flowable<Option<R>> = map { it.map(function) }
fun <T, R> Single<Option<T>>.mapDefined(function: (T) -> R): Single<Option<R>> = map { it.map(function) }

fun <T> Observable<Option<T>>.getOrElse(default: () -> T): Observable<T> = map { it.getOrElse(default) }
fun <T> Flowable<Option<T>>.getOrElse(default: () -> T): Flowable<T> = map { it.getOrElse(default) }
fun <T> Single<Option<T>>.getOrElse(default: () -> T): Single<T> = map { it.getOrElse(default) }


fun <L, R> Single<Either<L, R>>.doOnSuccessRight(call: (R) -> Unit): Single<Either<L, R>> =
    this.doOnSuccess {
        when (it) {
            is Either.Right -> call(it.r)
        }
    }

fun <L, R> Single<Either<L, R>>.doOnSuccessLeft(call: (L) -> Unit): Single<Either<L, R>> =
    this.doOnSuccess {
        when (it) {
            is Either.Left -> call(it.l)
        }
    }

// toTry

fun <T> Flowable<T>.toTry(): Flowable<Try<T>> = map { Try.Success(it) as Try<T> }.onErrorReturn { Try.Failure(it) }

fun <T> Flowable<Try<T>>.toEither(): Flowable<Either<Throwable, T>> = map { it.toEither() }

fun <T> Observable<T>.toTry(): Observable<Try<T>> = map { Try.Success(it) as Try<T> }.onErrorReturn { Try.Failure(it) }

fun <T> Observable<Try<T>>.toEither(): Observable<Either<Throwable, T>> = map { it.toEither() }

fun <T> Single<T>.toTry(): Single<Try<T>> = map { Try.Success(it) as Try<T> }.onErrorReturn { Try.Failure(it) }

fun <T> Single<Try<T>>.toEither(): Single<Either<Throwable, T>> = map { it.toEither() }

