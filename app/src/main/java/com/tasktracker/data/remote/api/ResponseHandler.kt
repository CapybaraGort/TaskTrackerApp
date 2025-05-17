package com.tasktracker.data.remote.api

import android.util.Log
import retrofit2.HttpException
import retrofit2.Response

class ResponseHandler {
    fun<T> handle(response: Response<ApiResponse<T>>): Result<T> {
        return runCatching {
            val body = response.body() ?: throw IllegalStateException("Empty body")
            body.data ?: throw HttpException(response)
        }.onFailure { e ->
            Log.e(null, e.message.toString())
        }
    }
}