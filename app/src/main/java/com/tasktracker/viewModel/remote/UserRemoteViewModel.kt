package com.tasktracker.viewModel.remote

import androidx.lifecycle.ViewModel
import com.tasktracker.data.remote.repository.RemoteUserRepository
import com.tasktracker.domain.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class UserRemoteViewModel @Inject constructor(
    private val remoteUserRepository: RemoteUserRepository
) : ViewModel() {

    var userUiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
        private set

    suspend fun getUserById(id: Int): User {
        return remoteUserRepository.getUserById(id)
    }

    suspend fun getUserByEmail(email: String): User? {
        return remoteUserRepository.getUserByEmail(email)
    }

    suspend fun addUser(user: User): User {
        val user = remoteUserRepository.addUser(user)
        return user
    }

    suspend fun deleteUser(id: Int): Int {
        val id = remoteUserRepository.deleteUserById(id)
        return id
    }

    suspend fun updateUser(id: Int, user: User): User {
        val user = remoteUserRepository.updateUser(user)
        return user
    }
}

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val result: List<User>) : UserUiState()
    data class Error(val error: String) : UserUiState()
}