package com.tasktracker.data.remote.auth

import com.yandex.authsdk.YandexAuthResult

sealed class AuthParams {
    object EmptyParams: AuthParams()
    data class YandexAuthParams(val result: YandexAuthResult): AuthParams()
    data class AnonymousAuthParams(val token: String): AuthParams()
}
