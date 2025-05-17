package com.tasktracker.data.remote.auth

import com.tasktracker.data.local.repository.SecurePrefsManager
import com.tasktracker.data.remote.auth.provider.AuthProvider
import com.tasktracker.domain.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthManager(
    private val providers: Map<AuthType, @JvmSuppressWildcards AuthProvider<*>>,
    private val securePrefsManager: SecurePrefsManager
) {
    companion object {
        var currentUser: User = User(name = "Anon", email = "")
            private set

        private val _currentAuthType = MutableStateFlow<AuthType>(AuthType.Anonymous)
        val currentAuthType: StateFlow<AuthType> get() = _currentAuthType

        fun setCurrentUser(user: User) {
            currentUser = user
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T: AuthParams> authenticate(authType: AuthType, params: T): AuthResult {
        val provider = providers[authType] as? AuthProvider<T>
            ?: throw NullPointerException("Provider not found")

        provider.authenticate(params).also { result ->
            return when(result) {
                AuthResult.Cancelled -> AuthResult.Cancelled
                is AuthResult.Failure -> AuthResult.Failure(result.error)
                is AuthResult.Success -> {
                    securePrefsManager.putString(authType.keyToken, result.token)
                    _currentAuthType.value = authType
                    AuthResult.Success(result.token)
                }
            }
        }
    }

    suspend fun logout(authType: AuthType) {
        val provider = providers[authType]
            ?: throw NullPointerException("Provider not found")

        provider.logout()
        securePrefsManager.remove(authType.keyToken)
    }

    suspend fun getToken(keyToken: KeyToken): String? {
        return securePrefsManager.getString(keyToken)
    }

    suspend fun isTokenValid(keyToken: KeyToken): Boolean {
        val isValid = securePrefsManager.isTokenValid(keyToken)
        if(isValid) {
            _currentAuthType.value = keyToken.toAuthType()
        }
        return isValid
    }
}