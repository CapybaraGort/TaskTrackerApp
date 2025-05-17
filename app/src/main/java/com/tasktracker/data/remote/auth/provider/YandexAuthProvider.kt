package com.tasktracker.data.remote.auth.provider

import com.tasktracker.data.remote.auth.AuthParams.YandexAuthParams
import com.tasktracker.data.remote.auth.AuthResult
import com.tasktracker.data.remote.auth.AuthType
import com.yandex.authsdk.YandexAuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class YandexAuthProvider() : AuthProvider<YandexAuthParams> {

    override val authType: AuthType = AuthType.Yandex
    private var token: String? = null

    override suspend fun authenticate(params: YandexAuthParams): AuthResult {
        return when (params.result) {
            is YandexAuthResult.Success -> {
                token = params.result.token.value
                AuthResult.Success(params.result.token.value)
            }

            is YandexAuthResult.Failure -> {
                AuthResult.Failure(params.result.exception)
            }

            is YandexAuthResult.Cancelled -> {
                AuthResult.Cancelled
            }
        }
    }

    override suspend fun logout() {
        revokeToken()
    }

    private suspend fun revokeToken() {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://oauth.yandex.ru/revoke_token?token=$token")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                if(response.isSuccessful) token = null
            } catch (e: Exception) {
                throw e
            }
        }

    }
}