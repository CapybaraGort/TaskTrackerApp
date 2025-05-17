package com.tasktracker.viewModel.remote.auth

import androidx.lifecycle.ViewModel
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthParams
import com.tasktracker.data.remote.auth.AuthType
import com.tasktracker.data.remote.auth.KeyToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AnonymousViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {
    private val _authState: MutableStateFlow<AnonymousAuthState> = MutableStateFlow(AnonymousAuthState.Idle)
    val authState: StateFlow<AnonymousAuthState> get() = _authState

    suspend fun validateToken() {
        setAuthState(AnonymousAuthState.Loading)
        try {
            val isValid = authManager.isTokenValid(KeyToken.AnonymousKeyToken)
            if(isValid) {
                setAuthState(AnonymousAuthState.Success)
            }
            else setAuthState(AnonymousAuthState.InvalidToken)
        } catch (e: Exception) {
            AnonymousAuthState.Error(e.message)
        }
    }

    suspend fun auth() {
        authManager.authenticate(AuthType.Anonymous, AuthParams.AnonymousAuthParams("anon"))
    }

    private fun setAuthState(state: AnonymousAuthState) {
        _authState.value = state
    }
}

sealed class AnonymousAuthState {
    object Idle : AnonymousAuthState()
    object Loading : AnonymousAuthState()
    object Success : AnonymousAuthState()
    object InvalidToken : AnonymousAuthState()
    data class Error(val message: String?)
}