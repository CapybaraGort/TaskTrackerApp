package com.tasktracker.data.remote.auth

sealed class AuthResult {
    data class Success(val token: String) : AuthResult()
    data class Failure(val error: Throwable) : AuthResult()
    object Cancelled : AuthResult()
}