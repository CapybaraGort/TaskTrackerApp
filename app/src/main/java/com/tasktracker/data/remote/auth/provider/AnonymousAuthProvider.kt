package com.tasktracker.data.remote.auth.provider

import com.tasktracker.data.remote.auth.AuthParams
import com.tasktracker.data.remote.auth.AuthResult
import com.tasktracker.data.remote.auth.AuthType

class AnonymousAuthProvider(override val authType: AuthType = AuthType.Anonymous) : AuthProvider<AuthParams.AnonymousAuthParams> {
    private var token: String? = null

    override suspend fun authenticate(params: AuthParams.AnonymousAuthParams): AuthResult {
        return try {
            when {
                params.token == "anon" -> {
                    token = params.token
                    AuthResult.Success(token = params.token)
                }
                else -> AuthResult.Cancelled
            }
        } catch (e: Exception) {
            return AuthResult.Failure(e)
        }
    }

    override suspend fun logout() {
        token = null
    }
}