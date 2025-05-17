package com.tasktracker.work

import android.content.Context
import androidx.work.*
import com.tasktracker.domain.entity.Task
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

fun scheduleTaskNotification(context: Context, task: Task, dateTime: LocalDateTime) {
    val currentTime = LocalDateTime.now()
    val delay = Duration.between(currentTime, dateTime).toMillis()

    if (delay > 0) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "title" to task.title,
                "description" to task.description
            ))
            .addTag(task.id.toString())
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            task.id.toString(),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}

fun cancelTaskNotification(context: Context, task: Task) {
    WorkManager.getInstance(context).cancelUniqueWork(task.id.toString())
}
