package com.wellio.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wellio.app.data.*
import com.wellio.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class WellioViewModel(application: Application) : AndroidViewModel(application) {
    val store = WellioStore(WellioDatabase.get(application))
    val planner = LocalPlanner()
    val state = store.state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    var busy = MutableStateFlow(false); private set
    val message = MutableStateFlow<String?>(null)
    fun dismissMessage() { message.value = null }
    fun action(block: suspend () -> Unit) {
        if (busy.value) return
        viewModelScope.launch {
            busy.value = true
            try { block() } catch (e: CancellationException) { throw e }
            catch (e: Exception) { message.value = e.message ?: "Something went wrong. Please try again." }
            finally { busy.value = false }
        }
    }
    fun saveProfile(profile: UserProfile, onSaved: () -> Unit = {}) = action {
        profile.validate()
        val days = withContext(Dispatchers.Default) { planner.week(profile, LocalDate.now()) }
        store.update { it.copy(profile = profile).mergeDays(days) }
        onSaved()
    }
    fun ensureDays() = action {
        val current = store.read()
        val profile = current.profile ?: return@action
        val missing = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
            .filter { date -> current.days.none { it.date == date.toString() } }
        if (missing.isNotEmpty()) {
            val days = withContext(Dispatchers.Default) { missing.map { planner.day(profile, it) } }
            store.update { old -> old.copy(days = (old.days + days.filter { day -> old.days.none { it.date == day.date } }).sortedBy { it.date }) }
        }
    }
    fun setTask(date: String, id: String, status: Status, time: String? = null) = action {
        if (time != null) java.time.LocalTime.parse(time)
        store.update { current -> current.changeDay(date) { day -> day.copy(tasks = day.tasks.map {
            if (it.id == id) it.copy(status = status, time = time ?: it.time) else it
        }) } }
    }

}
