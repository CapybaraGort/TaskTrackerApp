package com.tasktracker.work

import android.content.Context
import androidx.work.*
import com.tasktracker.domain.entity.Task
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import com.tasktracker.R

fun scheduleTaskNotification(context: Context, task: Task, dateTime: LocalDateTime, isDateTimeBegin: Boolean = false) {
    val currentTime = LocalDateTime.now()
    val delay = Duration.between(currentTime, dateTime).toMillis()
    val suffix = if (isDateTimeBegin) "_start" else "_deadline"
    val uniqueName = "${task.id}$suffix"

    if (delay > 0) {
        val title =
            if(isDateTimeBegin) context.getString(R.string.the_task_has_begun)
            else context.getString(R.string.task_timed_out)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "title" to title,
                "description" to task.title,
                "is_completed" to task.isCompleted
            ))
            .addTag(task.id.toString())
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}

fun cancelTaskNotification(context: Context, task: Task) {
    val workManager = WorkManager.getInstance(context)
    workManager.cancelUniqueWork("${task.id}_start")
    workManager.cancelUniqueWork("${task.id}_deadline")
}
