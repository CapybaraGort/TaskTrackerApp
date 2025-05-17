package com.tasktracker.data.remote.auth

sealed class AuthType {
    abstract val keyToken: KeyToken

    object Anonymous : AuthType() {
        override val keyToken: KeyToken = KeyToken.AnonymousKeyToken
    }

    object Yandex : AuthType() {
        override val keyToken: KeyToken = KeyToken.YandexKeyToken
    }
}