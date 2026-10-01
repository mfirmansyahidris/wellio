package com.fi.wellio

import com.fi.wellio.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import kotlinx.serialization.encodeToString
import com.fi.wellio.data.stateJson

class PlannerTest {
    private val today = LocalDate.of(2026, 10, 1)
    private val planner = LocalPlanner()
    @Test fun `week has seven dates and unique tasks`() {
        val days = planner.week(UserProfile(), today)
        assertEquals(7, days.size)
        assertEquals(today.plusDays(6).toString(), days.last().date)
        assertEquals(56, days.flatMap { it.tasks }.map { it.id }.distinct().size)
        assertTrue(days.all { it.meals.keys == mealKinds.toSet() && it.percent() == 0 })
    }
    @Test fun `vegan meals honor avoid list across week`() {
        val p = UserProfile(diet = "Vegan", avoid = listOf("banana", "milk", "peanuts"))
        assertTrue(planner.week(p, today).flatMap { it.meals.values }.all { allowedMeal(it, p) })
    }
    @Test(expected = IllegalArgumentException::class) fun `empty candidate pool never ignores restrictions`() {
        planner.week(UserProfile(avoid = listOf("oats", "tofu", "eggs")), today)
    }
    @Test fun `skipped days do not extend streak and yesterday can anchor it`() {
        val yesterday = planner.day(UserProfile(), today.minusDays(1))
        val done = yesterday.copy(tasks = yesterday.tasks.map { it.copy(status = Status.COMPLETED) })
        val skipped = planner.day(UserProfile(), today).let { it.copy(tasks = it.tasks.map { t -> t.copy(status = Status.SKIPPED) }) }
        assertEquals(1, streak(listOf(done, skipped), today))
        assertEquals(0, skipped.percent())
        assertEquals(0, streak(listOf(done), today.plusDays(1)))
    }
    @Test fun `state round trips for offline storage`() {
        val state = WellnessState(profile = UserProfile(), days = planner.week(UserProfile(), today))
        assertEquals(state, stateJson.decodeFromString<WellnessState>(stateJson.encodeToString(state)))
    }
    @Test fun `replacement does not reuse meal when alternatives exist`() {
        val p = UserProfile()
        val previous = planner.meal(p, TaskKind.LUNCH, 1)
        assertNotEquals(previous.name, planner.meal(p, TaskKind.LUNCH, 1, previous).name)
    }
    @Test fun `workout respects unavailable equipment and shorter reason`() {
        val p = UserProfile(equipment = listOf("Dumbbells"))
        assertTrue(planner.workout(p, reason = "Don't have the equipment").equipment.isEmpty())
        assertTrue(planner.workout(p, reason = "Need something shorter").minutes < p.minutes)
    }
}
