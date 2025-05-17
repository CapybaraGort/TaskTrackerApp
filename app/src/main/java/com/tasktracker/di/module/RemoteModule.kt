package com.tasktracker.di.module

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tasktracker.data.CombinedCategoryRepository
import com.tasktracker.data.adapter.LocalDateTimeAdapter
import com.tasktracker.data.local.repository.LocalCategoryRepository
import com.tasktracker.data.remote.api.CategoryApiService
import com.tasktracker.data.remote.api.ResponseHandler
import com.tasktracker.data.remote.api.TaskApiService
import com.tasktracker.data.remote.api.UserApiService
import com.tasktracker.data.remote.repository.RemoteCategoryRepository
import com.tasktracker.data.remote.repository.RemoteTaskRepository
import com.tasktracker.data.remote.repository.RemoteUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import javax.inject.Singleton

class ErrorHandlingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e: IOException) {
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(503)
                .message("Service Unavailable: " + e.message)
                .body("Сервер не доступен".toResponseBody(null))
                .build()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    private const val URL = "..."

    val moshi: Moshi = Moshi.Builder()
        .add(LocalDateTimeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(ErrorHandlingInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Singleton
    @Provides
    fun provideApiService(): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideTaskApiService(): TaskApiService {
        return retrofit.create(TaskApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideCategoryApiService(): CategoryApiService {
        return retrofit.create(CategoryApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRemoteCategoryRepository(
        apiService: CategoryApiService,
        handler: ResponseHandler
    ): RemoteCategoryRepository {
        return RemoteCategoryRepository(apiService, handler)
    }

    @Singleton
    @Provides
    fun provideCombinedCategoryRepository(
        remoteCategoryRepository: RemoteCategoryRepository,
        localCategoryRepository: LocalCategoryRepository
    ): CombinedCategoryRepository {
        return CombinedCategoryRepository(localCategoryRepository, remoteCategoryRepository)
    }

    @Singleton
    @Provides
    fun provideResponseHandler(): ResponseHandler {
        return ResponseHandler()
    }

    @Singleton
    @Provides
    fun provideRemoteTaskRepository(
        taskApiService: TaskApiService,
        handler: ResponseHandler
    ): RemoteTaskRepository {
        return RemoteTaskRepository(taskApiService, handler)
    }

    @Singleton
    @Provides
    fun provideUserRemoteRepository(
        userApiService: UserApiService,
        handler: ResponseHandler
    ): RemoteUserRepository {
        return RemoteUserRepository(userApiService, handler)
    }
}