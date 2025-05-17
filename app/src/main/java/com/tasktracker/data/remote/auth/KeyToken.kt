package com.tasktracker.data.remote.auth

import com.tasktracker.data.remote.auth.AuthType.Yandex
import okhttp3.OkHttpClient
import okhttp3.Request

sealed class KeyToken(val value: String) {
    abstract suspend fun isValid(token: String?): Boolean

    data object AnonymousKeyToken : KeyToken("anon_token") {
        override suspend fun isValid(token: String?): Boolean {
            if(token == null) return false

            return token == "anon"
        }

    }
    data object YandexKeyToken : KeyToken("oauth_token") {
        override suspend fun isValid(token: String?): Boolean {
            if(token == null) throw NullPointerException("token is null")
            return try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://login.yandex.ru/info?format=json")
                    .header("Authorization", "OAuth $token")
                    .build()

                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                throw Exception(e)
            }
        }
    }
}

fun KeyToken.toAuthType(): AuthType {
    return when(this) {
        KeyToken.YandexKeyToken -> Yandex
        KeyToken.AnonymousKeyToken -> AuthType.Anonymous
    }
}