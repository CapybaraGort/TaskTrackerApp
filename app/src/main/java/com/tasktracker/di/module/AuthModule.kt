package com.tasktracker.di.module

import com.tasktracker.data.local.repository.SecurePrefsManager
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthType
import com.tasktracker.data.remote.auth.provider.AnonymousAuthProvider
import com.tasktracker.data.remote.auth.provider.YandexAuthProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthManager(
        yandexAuthProvider: YandexAuthProvider,
        anonymousAuthProvider: AnonymousAuthProvider,
        securePrefsManager: SecurePrefsManager
    ): AuthManager {
        return AuthManager(
            providers = mapOf(
                AuthType.Yandex to yandexAuthProvider,
                AuthType.Anonymous to anonymousAuthProvider
            ),
            securePrefsManager = securePrefsManager
        )
    }

    @Singleton
    @Provides
    fun provideYandexAuthProvider(): YandexAuthProvider {
        return YandexAuthProvider()
    }

    @Singleton
    @Provides
    fun provideAnonymousAuthProvider(): AnonymousAuthProvider {
        return AnonymousAuthProvider()
    }
}