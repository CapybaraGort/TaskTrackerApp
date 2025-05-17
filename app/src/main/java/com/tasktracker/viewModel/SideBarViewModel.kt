package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SideBarViewModel @Inject constructor(
    val authManager: AuthManager
): ViewModel() {
    private val _authType = MutableStateFlow<AuthType>(AuthType.Anonymous)
    val authType get() = _authType.asStateFlow()

    init {
        viewModelScope.launch {
            AuthManager.currentAuthType.collectLatest {
                _authType.value = it
            }
        }
    }
}