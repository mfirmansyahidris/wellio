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
    val ai = com.wellio.app.ai.FirebasePlanner(application)
    private val reminders = com.wellio.app.reminders.ReminderScheduler(application)
    private fun observeReminders() {
        viewModelScope.launch {
            store.state.collect { current ->
                try { reminders.reconcile(current) }
                catch (e: CancellationException) { throw e }
                catch (_: Exception) { message.value = "Reminder scheduling failed. Reopen the app to retry." }
            }
        }
    }
    val state = store.state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    var busy = MutableStateFlow(false); private set
    val message = MutableStateFlow<String?>(null)
    init { observeReminders() }
    fun dismissMessage() { message.value = null }
    fun action(block: suspend () -> Unit) {
        if (busy.value) return
        busy.value = true
        viewModelScope.launch {
            try { block() } catch (e: CancellationException) { throw e }
            catch (e: Exception) { message.value = e.message ?: "Something went wrong. Please try again." }
            finally { busy.value = false }
        }
    }
    fun saveProfile(profile: UserProfile, onSaved: () -> Unit = {}) = action {
        profile.validate()
        val days = if (profile.aiEnabled) ai.week(profile, LocalDate.now()) else withContext(Dispatchers.Default) { planner.week(profile, LocalDate.now()) }
        store.update { it.copy(profile = profile).mergeDays(days) }
        onSaved()
    }
    fun ensureDays() = action {
        val current = store.read()
        val profile = current.profile ?: return@action
        val missing = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
            .filter { date -> current.days.none { it.date == date.toString() } }
        if (missing.isNotEmpty()) {
            val days = withContext(Dispatchers.Default) { missing.map { date ->
                try { planner.day(profile, date) }
                catch (e: IllegalArgumentException) {
                    val previous = current.days.lastOrNull() ?: throw e
                    buildDay(date, previous.workout, previous.meals, "Saved plan repeat")
                }
            } }
            store.update { old -> old.copy(days = (old.days + days.filter { day -> old.days.none { it.date == day.date } }).sortedBy { it.date }) }
        }
    }
    fun setTask(date: String, id: String, status: Status, time: String? = null) = action {
        if (time != null) java.time.LocalTime.parse(time)
        store.update { current -> current.changeDay(date) { day -> day.copy(tasks = day.tasks.map {
            if (it.id == id) it.copy(status = status, time = time ?: it.time) else it
        }) } }
    }

    fun completeSet(date: String, expected: Int) = action {
        store.update { current -> current.changeDay(date) { day ->
            val total = day.workout.exercises.sumOf { it.sets }
            val count = day.session?.completedSets ?: 0
            if (count != expected || count >= total) day else day.copy(session = SessionProgress(day.workout.title, count + 1))
        } }
    }
    fun finishWorkout(date: String) = action {
        store.update { current -> current.changeDay(date) { day ->
            if ((day.session?.completedSets ?: 0) < day.workout.exercises.sumOf { it.sets }) day
            else day.copy(tasks = day.tasks.map { if (it.kind == TaskKind.WORKOUT) it.copy(status = Status.COMPLETED) else it })
        } }
    }
    fun replaceWorkout(date: String, reason: String) = action {
        val current = store.read()
        val profile = current.profile ?: return@action
        val day = current.days.first { it.date == date }
        require(day.tasks.none { it.kind == TaskKind.WORKOUT && it.status == Status.COMPLETED }) { "Completed workouts stay in your history." }
        require(reason != "Need something shorter" || day.workout.minutes > 3) { "This routine is already very short. You can skip it or pause between sets." }
        val seed = listOf("Sit-to-stand", "Wall push-up", "Glute bridge", "Bird dog", "Standing calf raise", "Standing march").indexOf(day.workout.exercises.first().name).let { if (it < 0) 1 else it + 1 }
        val alternative = if (profile.aiEnabled) ai.workout(profile, day.workout, reason) else planner.workout(if (reason == "Need something shorter") profile.copy(minutes = day.workout.minutes) else profile, seed, reason)
        store.update { old -> old.changeDay(date) { it.copy(workout = alternative, session = null, source = if(profile.aiEnabled) "AI + saved plan" else "Local + saved plan",
            tasks = it.tasks.map { task -> if (task.kind == TaskKind.WORKOUT) task.copy(title = alternative.title) else task }) } }
    }

    fun replaceMeal(date: String, kind: TaskKind, reason: String) = action {
        val current = store.read()
        val profile = current.profile ?: return@action
        val day = current.days.first { it.date == date }
        require(day.tasks.none { it.kind == kind && it.status == Status.COMPLETED }) { "Completed meals stay in your history." }
        val meal = day.meals.getValue(kind)
        val alternative = if (profile.aiEnabled) ai.meal(profile, kind, meal, reason) else planner.meal(profile, kind, meal.name.hashCode(), meal, reason)
        require(alternative.name != meal.name) { "No other local meal matches your preferences. Your current meal is unchanged." }
        store.update { old -> old.changeDay(date) { it.copy(meals = it.meals + (kind to alternative), source = if(profile.aiEnabled) "AI + saved plan" else "Local + saved plan",
            tasks = it.tasks.map { task -> if (task.kind == kind) task.copy(title = alternative.name) else task }) } }
    }

    fun setReminder(date: String, id: String, enabled: Boolean) = action {
        store.update { current -> current.changeDay(date) { day -> day.copy(tasks = day.tasks.map {
            if (it.id == id) it.copy(reminder = enabled) else it
        }) } }
    }
    fun refreshReminders() { viewModelScope.launch { try { reminders.reconcile(store.read(), force = true) } catch (e: CancellationException) { throw e } catch (_: Exception) { message.value = "Could not refresh reminders." } } }

    fun enableAi(enabled: Boolean) = action {
        check(!enabled || ai.configured) { "Cloud AI is not configured." }
        store.update { it.copy(profile = it.profile?.copy(aiEnabled = enabled)) }
    }
    fun generateAiWeek() = action {
        val profile = store.read().profile ?: return@action
        val days = ai.week(profile, LocalDate.now())
        store.update { it.mergeDays(days) }
        message.value = "Your personalized week is ready."
    }

}
