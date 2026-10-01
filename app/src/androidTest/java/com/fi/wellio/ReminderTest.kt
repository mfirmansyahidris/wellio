package com.fi.wellio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkManager
import com.fi.wellio.domain.*
import com.fi.wellio.reminders.ReminderScheduler
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ReminderTest {
    @Test fun reschedulingReplacesWorkAndCompletionCancelsIt() = runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val scheduler=ReminderScheduler(context)
        val work=WorkManager.getInstance(context)
        val date=LocalDate.now().plusDays(2)
        val original=LocalPlanner().day(UserProfile(),date)
        val task=original.tasks.first().copy(reminder=true)
        val day=original.copy(tasks=listOf(task))
        var state=WellnessState(profile=UserProfile(),days=listOf(day))
        try {
            scheduler.reconcile(state,force=true)
            val first=work.getWorkInfosForUniqueWork(task.id).get().first { !it.state.isFinished }
            scheduler.reconcile(state,force=true)
            Assert.assertEquals(first.id,work.getWorkInfosForUniqueWork(task.id).get().first { !it.state.isFinished }.id)
            state=state.changeDay(date.toString()) {it.copy(tasks=listOf(task.copy(time="09:30",status=Status.RESCHEDULED)))}
            scheduler.reconcile(state)
            val second=work.getWorkInfosForUniqueWork(task.id).get().first { !it.state.isFinished }
            Assert.assertNotEquals(first.id,second.id)
            state=state.changeDay(date.toString()) {it.copy(tasks=it.tasks.map {t->t.copy(status=Status.COMPLETED)})}
            scheduler.reconcile(state)
            Assert.assertTrue(work.getWorkInfosForUniqueWork(task.id).get().all {it.state.isFinished})
        } finally {work.cancelUniqueWork(task.id).result.get()}
    }
}
