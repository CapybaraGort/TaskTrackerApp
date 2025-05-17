package com.tasktracker.data.remote.auth.provider

import com.tasktracker.data.remote.auth.AuthParams
import com.tasktracker.data.remote.auth.AuthResult
import com.tasktracker.data.remote.auth.AuthType

interface AuthProvider<in T : AuthParams> {
    val authType: AuthType
    suspend fun authenticate(params: T): AuthResult
    suspend fun logout()
}