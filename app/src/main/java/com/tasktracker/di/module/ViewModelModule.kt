package com.tasktracker.di.module

import com.tasktracker.data.CombinedTaskRepository
import com.tasktracker.data.local.dao.CategoryDao
import com.tasktracker.data.local.dao.TaskDao
import com.tasktracker.data.local.repository.LocalCategoryRepository
import com.tasktracker.data.local.repository.LocalTaskRepository
import com.tasktracker.data.remote.repository.RemoteTaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideTaskRepository(dao: TaskDao) : LocalTaskRepository {
        return LocalTaskRepository(dao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): LocalCategoryRepository {
        return LocalCategoryRepository(dao)
    }

    @Provides
    @Singleton
    fun provideCombinedTaskRepository(
        localTaskRepository: LocalTaskRepository,
        remoteTaskRepository: RemoteTaskRepository
    ): CombinedTaskRepository {
        return CombinedTaskRepository(localTaskRepository, remoteTaskRepository)
    }
}