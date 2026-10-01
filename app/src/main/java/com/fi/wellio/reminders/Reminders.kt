package com.fi.wellio.reminders

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
import com.fi.wellio.MainActivity
import com.fi.wellio.R
import com.fi.wellio.data.*
import com.fi.wellio.domain.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.*
import java.util.concurrent.TimeUnit

private const val TAG = "wellio-reminders"
private const val CHANNEL = "wellio-daily"
class ReminderScheduler(private val context: Context) {
    private var fingerprint: String? = null
    private val lock = Mutex()
    suspend fun reconcile(state: WellnessState, force: Boolean = false) = lock.withLock { withContext(Dispatchers.IO) {
        val signature = state.days.flatMap { day -> day.tasks.map { "${day.date}|${it.id}|${it.time}|${it.status}|${it.reminder}" } }.joinToString() + ZoneId.systemDefault().id
        if (!force && signature == fingerprint) return@withContext
        val manager = WorkManager.getInstance(context)
        val pending = manager.getWorkInfosByTag(TAG).get().filter { !it.state.isFinished }
        val now = ZonedDateTime.now()
        for (day in state.days) for (task in day.tasks) {
            val active = pending.filter { "task:${task.id}" in it.tags }
            val enabled = task.reminder && task.status !in listOf(Status.COMPLETED, Status.SKIPPED)
            val due = LocalDate.parse(day.date).atTime(LocalTime.parse(task.time)).atZone(now.zone)
            val dueTag = "due:${due.toInstant()}"
            // Keep an already-due job intact: WorkManager may be waiting for the OS to run it.
            if (enabled && active.any { dueTag in it.tags }) continue
            if (active.isNotEmpty()) manager.cancelUniqueWork(task.id).result.get()
            val delay = Duration.between(now, due).toMillis()
            if (!enabled || delay <= 0) continue
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("date" to day.date, "taskId" to task.id))
                .addTag(TAG).addTag("task:${task.id}").addTag(dueTag).build()
            manager.enqueueUniqueWork(task.id, ExistingWorkPolicy.REPLACE, request).result.get()
        }
        fingerprint = signature
    } }
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
