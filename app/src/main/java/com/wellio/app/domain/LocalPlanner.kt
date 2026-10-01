package com.wellio.app.domain

import java.time.LocalDate

class LocalPlanner {
    fun week(profile: UserProfile, start: LocalDate): List<DayPlan> {
        profile.validate()
        return (0..6).map { offset -> day(profile, start.plusDays(offset.toLong())) }
    }
    fun day(profile: UserProfile, date: LocalDate): DayPlan {
        val seed = date.toEpochDay().toInt()
        val workout = workout(profile, seed)
        val meals = mealKinds.associateWith { meal(profile, it, seed + it.ordinal) }
        return DayPlan(date.toString(), workout, meals, listOf(
            DailyTask("$date-water", TaskKind.WATER, "A glass of water", "07:00"),
            DailyTask("$date-breakfast", TaskKind.BREAKFAST, meals.getValue(TaskKind.BREAKFAST).name, "07:30"),
            DailyTask("$date-lunch", TaskKind.LUNCH, meals.getValue(TaskKind.LUNCH).name, "12:30"),
            DailyTask("$date-walk", TaskKind.WALK, "A little fresh air", "15:00"),
            DailyTask("$date-snack", TaskKind.SNACK, meals.getValue(TaskKind.SNACK).name, "16:00"),
            DailyTask("$date-workout", TaskKind.WORKOUT, workout.title, "17:30"),
            DailyTask("$date-dinner", TaskKind.DINNER, meals.getValue(TaskKind.DINNER).name, "19:00"),
            DailyTask("$date-winddown", TaskKind.WIND_DOWN, "Slow down & unwind", "22:00")
        ))
    }
    fun workout(p: UserProfile, seed: Int = 0, reason: String = ""): Workout {
        val gentle = reason == "Too difficult" || p.activity == "Mostly sitting"
        val shorter = reason == "Need something shorter"
        val minutes = if (shorter) (p.minutes / 2).coerceAtLeast(5) else p.minutes
        val useWeights = "Dumbbells" in p.equipment && reason != "Don't have the equipment"
        val sets = if (minutes <= 10 || gentle) 1 else if (p.level == "Intermediate") 3 else 2
        val reps = if (gentle) 8 else if (reason == "Too easy") 14 else 12
        val variants = listOf(
            Exercise("Sit-to-stand", sets, reps, instruction = "Use a stable chair. Stand slowly, then sit with control."),
            Exercise("Wall push-up", sets, reps, instruction = "Hands on a wall at shoulder height. Bend your elbows slowly and push back."),
            Exercise("Glute bridge", sets, reps, instruction = "Lie on your back with knees bent. Lift your hips gently, then lower."),
            Exercise("Bird dog", sets, reps, instruction = "On hands and knees, extend opposite arm and leg. Alternate slowly."),
            Exercise("Standing calf raise", sets, reps, instruction = "Hold a stable support. Rise onto your toes and lower slowly."),
            Exercise("Standing march", sets, seconds = 40, instruction = "March at a comfortable pace, keeping your shoulders relaxed.")
        )
        val count = if (minutes <= 10) 3 else 5
        var chosen = (0 until count).map { variants[Math.floorMod(seed + it, variants.size)] }
        if (useWeights) chosen = chosen.dropLast(1) + Exercise("Dumbbell row", sets, reps,
            instruction = "Choose a comfortable weight. Hinge at the hips, draw elbows back, then lower with control.")
        if (p.location == "Outdoor") chosen = chosen.dropLast(1) + Exercise("Comfortable walk", 1,
            seconds = if (minutes <= 10) 60 else 180, instruction = "Walk on a level path at a pace where you can talk comfortably.")
        val title = if (useWeights) "Everyday strength" else if (Math.floorMod(seed, 2) == 0) "Feel-good full body" else "Move at your pace"
        return Workout(title, "Warm up gently, move at your own pace, and finish with an easy stretch. Stop if a movement hurts.",
            minutes, if (gentle) "Beginner" else p.level, p.location, if (useWeights) listOf("Dumbbells") else emptyList(), chosen)
    }
    fun meal(p: UserProfile, kind: TaskKind, seed: Int, previous: Meal? = null, reason: String = ""): Meal {
        val pool = Catalog.meals.filter { allowedMeal(it, p) && (if (kind == TaskKind.BREAKFAST) "Breakfast" in it.tags else if (kind == TaskKind.SNACK) "Snack" in it.tags else "Main" in it.tags) }
        val alternatives = pool.filter { it.name != previous?.name }.ifEmpty { pool }
        require(alternatives.isNotEmpty()) { "No local meal matches these food preferences. Adjust foods to avoid or use AI when configured." }
        val sorted = alternatives.sortedByDescending { m ->
            (if (m.cuisine == p.cuisine) 4 else 0) +
            (if (("Quick preparation" in p.priorities || reason == "Something quicker") && m.minutes <= 10) 3 else 0) +
            (if (("Budget friendly" in p.priorities || reason == "Something cheaper") && m.budget) 3 else 0) +
            (if (("High protein" in p.priorities || p.diet == "High Protein") && "High protein" in m.tags) 3 else 0) +
            (if ("Lots of vegetables" in p.priorities && "Vegetables" in m.tags) 2 else 0)
        }
        val preferred = if (reason in listOf("Something quicker", "Something cheaper") || p.cuisine != "Anything" || p.diet == "High Protein") sorted.take(2) else sorted
        return preferred[Math.floorMod(seed, preferred.size)]
    }
}

fun allowedMeal(m: Meal, p: UserProfile): Boolean {
    if (p.diet == "Vegan" && "Vegan" !in m.tags) return false
    if (p.diet == "Vegetarian" && "Vegetarian" !in m.tags && "Vegan" !in m.tags) return false
    if (p.diet == "Low Carb" && "Low carb" !in m.tags) return false
    val haystack = (m.ingredients + m.name).joinToString(" ").lowercase()
    return p.avoid.none { avoid ->
        val word = avoid.trim().lowercase()
        val aliases = when (word) {
            "seafood", "makanan laut" -> listOf("fish", "shrimp", "tuna", "salmon", "prawn", "ikan", "udang", "seafood")
            "milk", "susu", "dairy" -> listOf("milk", "yogurt", "cheese", "butter", "cream", "susu")
            "peanuts", "kacang tanah" -> listOf("peanut", "kacang tanah")
            "spicy food", "pedas" -> listOf("chili", "chilli", "sambal", "spicy")
            else -> listOf(word)
        }
        word.isNotBlank() && aliases.any { it in haystack }
    }
}
