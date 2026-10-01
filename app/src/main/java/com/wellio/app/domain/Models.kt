package com.wellio.app.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable enum class Goal(val label: String) {
    ACTIVE("Stay active"), STRONGER("Get stronger"), EAT_BETTER("Eat better"),
    WEIGHT("Weight management"), HABITS("Better habits"), WELLNESS("Overall wellness")
}
@Serializable enum class Status { PENDING, COMPLETED, SKIPPED, RESCHEDULED }
@Serializable enum class TaskKind { WORKOUT, BREAKFAST, LUNCH, SNACK, DINNER, WATER, WALK, WIND_DOWN }
@Serializable data class UserProfile(
    val name: String = "", val goals: List<Goal> = listOf(Goal.ACTIVE),
    val activity: String = "Mostly sitting", val level: String = "Beginner",
    val minutes: Int = 20, val location: String = "Home", val equipment: List<String> = emptyList(),
    val diet: String = "Regular", val cuisine: String = "Anything",
    val priorities: List<String> = listOf("Simple cooking"), val avoid: List<String> = emptyList(),
    val aiEnabled: Boolean = false
)
@Serializable data class Exercise(val name: String, val sets: Int, val reps: Int = 0,
    val seconds: Int = 0, val restSeconds: Int = 30, val instruction: String)
@Serializable data class Workout(val title: String, val description: String, val minutes: Int,
    val level: String, val location: String, val equipment: List<String> = emptyList(), val exercises: List<Exercise>)
@Serializable data class Meal(val name: String, val minutes: Int, val ingredients: List<String>,
    val instructions: List<String>, val tags: List<String>, val cuisine: String = "Asian", val budget: Boolean = true)
@Serializable data class DailyTask(val id: String, val kind: TaskKind, val title: String, val time: String,
    val status: Status = Status.PENDING, val reminder: Boolean = false)
@Serializable data class SessionProgress(val workoutTitle: String, val completedSets: Int = 0)
@Serializable data class DayPlan(val date: String, val workout: Workout, val meals: Map<TaskKind, Meal>,
    val tasks: List<DailyTask>, val source: String = "Local plan", val session: SessionProgress? = null)
@Serializable data class WellnessState(val version: Int = 1, val profile: UserProfile? = null, val days: List<DayPlan> = emptyList())

val mealKinds = listOf(TaskKind.BREAKFAST, TaskKind.LUNCH, TaskKind.SNACK, TaskKind.DINNER)
fun DayPlan.completed() = tasks.count { it.status == Status.COMPLETED }
fun DayPlan.percent() = if (tasks.isEmpty()) 0 else completed() * 100 / tasks.size
fun streak(days: List<DayPlan>, today: LocalDate): Int {
    val completed = days.filter { it.completed() > 0 }.map { it.date }.toSet()
    var date = if (today.toString() in completed) today else today.minusDays(1)
    var result = 0
    while (date.toString() in completed) { result++; date = date.minusDays(1) }
    return result
}
fun UserProfile.validate() {
    require(goals.size in 1..2 && goals.distinct().size == goals.size) { "Choose one or two goals." }
    require(minutes in listOf(10, 20, 30, 45, 60)) { "Choose an available workout duration." }
    require(level in listOf("Beginner", "Intermediate"))
    require(name.length <= 40 && avoid.all { it.length <= 40 } && avoid.size <= 20)
}
