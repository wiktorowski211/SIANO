package com.siano

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenPreferences @Inject constructor(context: Context) {

    companion object {
        const val PREFERENCES_ID = "authorization"
        const val TOKEN = "auth_token"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_ID, 0)

    fun get(): String = sharedPreferences.getString(TOKEN, "").orEmpty()

    fun set(token: String) = sharedPreferences.edit().putString(TOKEN, token).apply()

    fun clear() = sharedPreferences.edit().clear().apply()
}