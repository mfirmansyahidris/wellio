package com.fi.wellio

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fi.wellio.data.*
import com.fi.wellio.domain.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class StoreTest {
    @Test fun concurrentTransactionsRetainBothChanges() = runBlocking {
        val db=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext,WellioDatabase::class.java).build()
        try {
            val store=WellioStore(db)
            val date=LocalDate.now().toString()
            store.update {WellnessState(profile=UserProfile(),days=LocalPlanner().week(UserProfile(),LocalDate.now()))}
            coroutineScope {
                listOf(TaskKind.WATER,TaskKind.WALK).map {kind -> async(Dispatchers.IO) {
                    store.update {s -> s.changeDay(date) {d -> d.copy(tasks=d.tasks.map {if(it.kind==kind) it.copy(status=Status.COMPLETED) else it})}}
                }}.awaitAll()
            }
            Assert.assertEquals(2,store.read().days.first().completed())
        } finally {db.close()}
    }
}
