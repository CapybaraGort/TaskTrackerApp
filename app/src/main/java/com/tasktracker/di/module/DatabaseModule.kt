package com.tasktracker.di.module

import android.content.Context
import androidx.room.Room
import com.tasktracker.data.local.dao.CategoryDao
import com.tasktracker.data.local.dao.TaskDao
import com.tasktracker.data.local.database.DatabaseCallback
import com.tasktracker.data.local.database.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTaskDao(db : TaskDatabase) : TaskDao {
        return db.getTaskDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db : TaskDatabase): CategoryDao {
        return db.getCategoryDao()
    }

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideDatabaseCallback(
        provider: Provider<TaskDatabase>,
        scope: CoroutineScope,
        @ApplicationContext context: Context
    ): DatabaseCallback {
        return DatabaseCallback(provider, scope, context)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "taskDatabase.db"
        )
            .addCallback(callback)
            .fallbackToDestructiveMigration(false)
            .build()
    }
}