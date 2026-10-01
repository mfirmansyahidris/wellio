package fi.dev.wellio.domain

fun WellnessState.mergeDays(incoming: List<DayPlan>): WellnessState {
    val replacements = incoming.associateBy { it.date }
    val merged = incoming.map { fresh ->
        val old = days.find { it.date == fresh.date } ?: return@map fresh
        val completedKinds = old.tasks.filter { it.status == Status.COMPLETED }.map { it.kind }.toSet()
        val workout = if (TaskKind.WORKOUT in completedKinds) old.workout else fresh.workout
        val meals = fresh.meals.mapValues { (kind, meal) -> if (kind in completedKinds) old.meals.getValue(kind) else meal }
        fresh.copy(workout = workout, meals = meals, session = if (old.workout == workout) old.session else null,
            tasks = fresh.tasks.map { task ->
                val previous = old.tasks.find { it.kind == task.kind }
                if (previous == null) task else task.copy(
                    title = if (task.kind in completedKinds) previous.title else task.title,
                    status = previous.status, time = previous.time, reminder = previous.reminder)
            })
    }
    return copy(days = (days.filter { it.date !in replacements } + merged).sortedBy { it.date })
}
fun WellnessState.changeDay(date: String, transform: (DayPlan) -> DayPlan) =
    copy(days = days.map { if (it.date == date) transform(it) else it })
