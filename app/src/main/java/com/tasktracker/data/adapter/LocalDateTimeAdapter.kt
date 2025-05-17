package com.tasktracker.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

class LocalDateTimeAdapter {
    @ToJson
    fun toJson(value: LocalDateTime): String = value.toString()

    @FromJson
    fun fromJson(value: String): LocalDateTime = LocalDateTime.parse(value)
}