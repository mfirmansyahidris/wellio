package com.fi.wellio

import com.fi.wellio.ai.*
import com.fi.wellio.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class PlanValidationTest {
    private val start = LocalDate.of(2026,10,1)
    private val p = UserProfile()
    private val planner = LocalPlanner()
    private fun draft(): WeekDraft = WeekDraft(planner.week(p,start).map { DayDraft(it.date,it.workout,it.meals.getValue(TaskKind.BREAKFAST),it.meals.getValue(TaskKind.LUNCH),it.meals.getValue(TaskKind.SNACK),it.meals.getValue(TaskKind.DINNER)) })
    @Test fun `valid AI draft maps into stable task ids`() {
        val result=PlanValidation.week(draft(),p,start)
        assertEquals(planner.week(p,start).map { it.tasks.map { t -> t.id } },result.map { it.tasks.map { t -> t.id } })
    }
    @Test(expected=IllegalArgumentException::class) fun `duplicate dates rejected`() {
        val d=draft();PlanValidation.week(d.copy(days=List(7){d.days.first()}),p,start)
    }
    @Test(expected=IllegalArgumentException::class) fun `extra equipment rejected`() {
        PlanValidation.workout(planner.workout(p).copy(equipment=listOf("Barbell")),p)
    }
    @Test(expected=IllegalArgumentException::class) fun `unbounded set count rejected`() {
        val w=planner.workout(p); PlanValidation.workout(w.copy(exercises=listOf(w.exercises.first().copy(sets=999))),p)
    }
    @Test(expected=IllegalArgumentException::class) fun `mislabelled vegan meal rejected`() {
        val m=Catalog.meals.first().copy(ingredients=listOf("Chicken"),tags=listOf("Vegan"))
        PlanValidation.meal(m,p.copy(diet="Vegan"))
    }
    @Test(expected=IllegalArgumentException::class) fun `avoided ingredient rejected`() {
        PlanValidation.meal(Catalog.meals.first(),p.copy(avoid=listOf("banana")))
    }
    @Test fun `refresh preserves completions schedule reminders and history`() {
        val old=planner.week(p,start)
        val day=old.first().copy(tasks=old.first().tasks.map { if(it.kind==TaskKind.LUNCH) it.copy(status=Status.COMPLETED,reminder=true,time="13:45") else it })
        val state=WellnessState(profile=p,days=listOf(planner.day(p,start.minusDays(1)),day)+old.drop(1))
        val fresh=planner.week(p.copy(diet="Vegan"),start)
        val merged=state.mergeDays(fresh)
        val updated=merged.days.first {it.date==start.toString()}
        assertEquals(8,merged.days.size)
        assertEquals(day.meals[TaskKind.LUNCH],updated.meals[TaskKind.LUNCH])
        assertEquals(day.tasks.first {it.kind==TaskKind.LUNCH},updated.tasks.first {it.kind==TaskKind.LUNCH})
    }
}
