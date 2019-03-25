package com.siano.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import org.funktionale.either.Either
import org.funktionale.option.Option
import java.io.IOException

object ErrorHandler {
    // TODO Error handling
    fun show(view: View): (Option<DefaultError>) -> Unit {
        return { throwable ->
            val error = throwable.orNull()

            if (error != null) {
                val message = when (error) {
                    is NoNetworkError -> "No network connection"
                    else -> "Unknown error"
                }
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

fun <T1> Single<T1>.handleEitherRestErrors(): Single<Either<DefaultError, T1>> =
    toTry()
        .toEither()
        .mapLeft { it.toDefaultError() }

fun Throwable.toDefaultError(): DefaultError = when (this) {
    is NoNetworkException -> NoNetworkError
    is BlockedNetworkException -> BlockedNetworkError
    is IOException -> NetworkError(this)
    else -> UnknownClientError("Fatal error ( " + this.javaClass.name + " )", this)
}

// Errors

interface DefaultError

object NoNetworkError : DefaultError
object BlockedNetworkError : DefaultError
object NotYetLoadedError : DefaultError
object EmptyError : DefaultError
object NotLoggedInError : DefaultError
object LoggedOutError : DefaultError
object SearchQueryEmptyError : DefaultError
object SearchTooShortQueryError : DefaultError
object NotFoundError : DefaultError
data class LoggedOutUserError(val userId: String) : DefaultError
data class NetworkError(val exception: IOException) : DefaultError
data class NoPermissionError(val missingPermissions: List<String>) : DefaultError
data class UnknownClientError(val userMessage: String, val info: Any? = null) : DefaultError
data class UnknownServerError(val userMessage: String, val info: Any? = null) : DefaultError


// Exceptions

class EmptyException : Exception()

class SearchQueryEmptyException : Exception()

class SearchTooShortQueryException : Exception()

class NotYetLoadedException : Exception()

class NoNetworkException : IOException()

class BlockedNetworkException : IOException()

