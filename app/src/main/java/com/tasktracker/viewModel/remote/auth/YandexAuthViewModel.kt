package com.tasktracker.viewModel.remote.auth

import androidx.lifecycle.ViewModel
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthParams
import com.tasktracker.data.remote.auth.AuthResult
import com.tasktracker.data.remote.auth.AuthType
import com.tasktracker.data.remote.auth.KeyToken
import com.yandex.authsdk.YandexAuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class YandexAuthViewModel @Inject constructor (
    private val authManager: AuthManager
): ViewModel() {

    private val _authState: MutableStateFlow<YandexAuthState> = MutableStateFlow(YandexAuthState.Idle)
    val authState: StateFlow<YandexAuthState> get() = _authState

    suspend fun validateToken() {
        setAuthState(YandexAuthState.Loading)
        try {
            val isValid = authManager.isTokenValid(KeyToken.YandexKeyToken)
            if(isValid)
                setAuthState(YandexAuthState.Success)
            else
                setAuthState(YandexAuthState.InvalidToken)
        } catch (e: Exception) {
            YandexAuthState.Error(e.message)
        }
    }

    fun setAuthState(state: YandexAuthState) {
        _authState.value = state
    }

    suspend fun getToken(): String? {
        return authManager.getToken(KeyToken.YandexKeyToken)
    }

    suspend fun handleResult(result: YandexAuthResult): AuthResult {
        val authResult = authManager.authenticate<AuthParams.YandexAuthParams>(
            authType = AuthType.Yandex,
            params = AuthParams.YandexAuthParams(result)
        )
        return authResult
    }

    suspend fun getUserInfo(token: String): Result<JSONObject> {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://login.yandex.ru/info?format=json")
                    .header("Authorization", "OAuth $token")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@withContext Result.failure(
                        Exception("Пустой ответ")
                    )
                    Result.success(JSONObject(responseBody))
                } else {
                    Result.failure(Exception("Ошибка запроса: HTTP ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

sealed class YandexAuthState {
    object Idle : YandexAuthState()
    object Loading : YandexAuthState()
    object Success : YandexAuthState()
    object InvalidToken : YandexAuthState()
    data class Error(val message: String?) : YandexAuthState()
}