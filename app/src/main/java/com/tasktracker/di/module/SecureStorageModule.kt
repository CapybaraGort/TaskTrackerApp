package com.tasktracker.di.module

import android.content.Context
import android.content.SharedPreferences
import com.tasktracker.data.local.repository.SecurePrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecureStorageModule {

    @Provides
    @Singleton
    fun provideSecurePrefsManager(@ApplicationContext context: Context): SecurePrefsManager {
        return SecurePrefsManager(context)
    }
}