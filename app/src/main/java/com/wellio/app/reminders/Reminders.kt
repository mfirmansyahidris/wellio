package com.wellio.app.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.wellio.app.MainActivity
import com.wellio.app.R
import com.wellio.app.data.*
import com.wellio.app.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.*
import java.util.concurrent.TimeUnit

private const val TAG = "wellio-reminders"
private const val CHANNEL = "wellio-daily"
class ReminderScheduler(private val context: Context) {
    private var fingerprint: String? = null
    suspend fun reconcile(state: WellnessState, force: Boolean = false) = withContext(Dispatchers.IO) {
        val signature = state.days.flatMap { day -> day.tasks.map { "${day.date}|${it.id}|${it.time}|${it.status}|${it.reminder}|${it.title}" } }.joinToString() + ZoneId.systemDefault().id
        if (!force && signature == fingerprint) return@withContext
        val manager = WorkManager.getInstance(context)
        manager.cancelAllWorkByTag(TAG).result.get()
        val now = ZonedDateTime.now()
        for (day in state.days) for (task in day.tasks) {
            if (!task.reminder || task.status in listOf(Status.COMPLETED, Status.SKIPPED)) continue
            val due = LocalDate.parse(day.date).atTime(LocalTime.parse(task.time)).atZone(now.zone)
            val delay = Duration.between(now, due).toMillis()
            if (delay <= 0) continue
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("date" to day.date, "taskId" to task.id))
                .addTag(TAG).build()
            manager.enqueueUniqueWork(task.id, ExistingWorkPolicy.REPLACE, request).result.get()
        }
        fingerprint = signature
    }
}
class ReminderWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val date = inputData.getString("date") ?: return Result.success()
        val id = inputData.getString("taskId") ?: return Result.success()
        val day = WellioStore(WellioDatabase.get(applicationContext)).read().days.find { it.date == date } ?: return Result.success()
        val task = day.tasks.find { it.id == id } ?: return Result.success()
        val now = LocalDateTime.now()
        if (!task.reminder || task.status in listOf(Status.COMPLETED, Status.SKIPPED) ||
            day.date != now.toLocalDate().toString() || now.isBefore(LocalDate.parse(date).atTime(LocalTime.parse(task.time)))) return Result.success()
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return Result.success()
        val manager = NotificationManagerCompat.from(applicationContext)
        if (!manager.areNotificationsEnabled()) return Result.success()
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Daily routine", NotificationManager.IMPORTANCE_DEFAULT))
        val intent = PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification).setContentTitle(task.title)
            .setContentText("A little time for you. Your Wellio plan is ready.")
            .setContentIntent(intent).setAutoCancel(true).build()
        try { manager.notify(task.id.hashCode(), notification) } catch (_: SecurityException) { return Result.success() }
        return Result.success()
    }
}
