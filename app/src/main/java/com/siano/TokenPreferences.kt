package com.siano

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenPreferences @Inject constructor(context: Context) {

    companion object {
        const val PREFERENCES_ID = "auth_prefs"
        const val TOKEN = "token"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_ID, 0)

    fun edit(): Editor = Editor(sharedPreferences)

    fun getToken(): String = sharedPreferences.getString(TOKEN, null).orEmpty()

    inner class Editor(sharedPreferences: SharedPreferences) {
        private val editor: SharedPreferences.Editor = sharedPreferences.edit()

        fun setToken(token: String) = editor.putString(TOKEN, token).apply()

        fun clear(): Editor {
            editor.clear()
            return this
        }
    }
}