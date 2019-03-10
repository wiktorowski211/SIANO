package com.siano.utils

import android.util.Base64

object TokenUtils {

    fun create(username: String, password: String): String {
        return "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
    }
}