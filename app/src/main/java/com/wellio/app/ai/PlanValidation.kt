package com.wellio.app.ai

import com.wellio.app.domain.*
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable data class WeekDraft(val days: List<DayDraft>)
@Serializable data class DayDraft(val date: String, val workout: Workout, val breakfast: Meal, val lunch: Meal, val snack: Meal, val dinner: Meal)
object PlanValidation {
    fun workout(w: Workout, p: UserProfile) {
        require(w.title.isNotBlank() && w.title.length <= 100 && w.description.length <= 1000)
        require(w.minutes in 1..p.minutes && w.location == p.location)
        require(w.level == p.level || w.level == "Beginner")
        require(w.equipment.all { it in p.equipment } && w.equipment.size <= 7)
        require(w.exercises.size in 1..12)
        w.exercises.forEach { e ->
            require(e.name.isNotBlank() && e.name.length <= 100 && e.instruction.isNotBlank() && e.instruction.length <= 1000)
            require(e.sets in 1..6 && e.restSeconds in 0..180)
            require((e.reps in 1..50 && e.seconds == 0) || (e.seconds in 1..600 && e.reps == 0))
        }
    }
    fun meal(m: Meal, p: UserProfile) {
        require(m.name.isNotBlank() && m.name.length <= 100 && m.minutes in 1..120)
        require(m.ingredients.size in 1..20 && m.instructions.size in 1..12 && m.tags.size in 1..15)
        require((m.ingredients + m.instructions + m.tags).all { it.isNotBlank() && it.length <= 1000 })
        require(allowedMeal(m,p)) { "Generated meal did not match your food preferences." }
        val ingredients = m.ingredients.joinToString(" ").lowercase()
        val meat = listOf("chicken", "beef", "pork", "fish", "shrimp", "salmon", "tuna", "ayam", "daging", "ikan", "udang")
        if(p.diet in listOf("Vegan","Vegetarian")) require(meat.none { it in ingredients })
        if(p.diet == "Vegan") require(listOf("egg", "milk", "cheese", "yogurt", "honey", "butter", "telur", "susu").none { it in ingredients })
    }
    fun week(draft: WeekDraft, p: UserProfile, start: LocalDate): List<DayPlan> {
        require(draft.days.size == 7) { "AI response must contain exactly seven days." }
        val expected = (0..6).map { start.plusDays(it.toLong()).toString() }.toSet()
        require(draft.days.map { it.date }.toSet() == expected) { "AI response contained incorrect or duplicate dates." }
        return draft.days.sortedBy { it.date }.map { d ->
            workout(d.workout,p)
            val meals=mapOf(TaskKind.BREAKFAST to d.breakfast,TaskKind.LUNCH to d.lunch,TaskKind.SNACK to d.snack,TaskKind.DINNER to d.dinner)
            meals.values.forEach { meal(it,p) }
            buildDay(LocalDate.parse(d.date), d.workout, meals, "AI plan")
        }
    }
}
