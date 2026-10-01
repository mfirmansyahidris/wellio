package fi.dev.wellio

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.dev.wellio.data.*
import fi.dev.wellio.domain.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class WellioFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val store get() = WellioStore(WellioDatabase.get(InstrumentationRegistry.getInstrumentation().targetContext))
    @Before fun reset() { runBlocking { store.update { WellnessState() } }; compose.waitForIdle() }
    private fun closeSoftKeyboard() {
        compose.runOnUiThread {
            val activity=compose.activity
            (activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager).hideSoftInputFromWindow(activity.window.decorView.windowToken,0)
            activity.currentFocus?.clearFocus()
        }
        compose.waitForIdle()
    }
    private fun click(text: String) { compose.onNodeWithText(text).performScrollTo().performClick() }
    private fun waitText(text: String) { compose.waitUntil(15000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() } }
    @Test fun onboardingTaskPersistenceMealSwapAndWorkoutCompletion() {
        waitText("Let's get started");click("Let's get started")
        compose.onNodeWithText("Your first name (optional)").performTextInput("Ari")
        closeSoftKeyboard()
        click("Eat better")
        click("Continue")
        click("10 min")
        click("Continue")
        click("Vegan")
        compose.onNodeWithText("Foods to avoid").performScrollTo().performTextInput("milk, peanuts")
        closeSoftKeyboard()
        click("Continue")
        click("Create my plan")
        waitText("Your daily rhythm")
        click("A glass of water")
        compose.onNodeWithText("Done").performClick()
        compose.waitUntil(10000) { runBlocking { store.read().days.first().completed()==1 } }
        compose.activityRule.scenario.recreate()
        waitText("Your daily rhythm")
        compose.onNodeWithText("12%").assertExists()
        compose.onNodeWithText("Meals").performClick()
        val before=runBlocking {store.read().days.first().meals.getValue(TaskKind.BREAKFAST).name}
        click(before)
        click("Replace meal")
        compose.onNodeWithText("Surprise me").performClick()
        compose.waitUntil(10000) {runBlocking {store.read().days.first().meals.getValue(TaskKind.BREAKFAST).name != before}}
        val after=runBlocking {store.read()}
        Assert.assertEquals(1,after.days.first().completed())
        Assert.assertTrue(after.days.first().meals.values.all {allowedMeal(it,after.profile!!)})
        compose.onNodeWithText("Workout").performClick()
        click("Start workout")
        val total=after.days.first().workout.exercises.sumOf {it.sets}
        for(i in 0 until total) {
            click("Complete set")
            compose.waitUntil(10000) {runBlocking {(store.read().days.first().session?.completedSets ?: 0) == i+1}}
        }
        click("Finish workout")
        compose.waitUntil(10000) {runBlocking {store.read().days.first().tasks.first {it.kind==TaskKind.WORKOUT}.status==Status.COMPLETED}}
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithText("1 workouts completed").assertExists()
    }
    @Test fun taskRescheduleSkipUndoAndProfileRefresh() {
        val today=LocalDate.now()
        runBlocking {store.update {WellnessState(profile=UserProfile(name="Test"),days=LocalPlanner().week(UserProfile(),today))}}
        waitText("Your daily rhythm")
        click("A glass of water")
        compose.onNodeWithText("Time (HH:mm)").performTextReplacement("99:99")
        compose.onNodeWithText("Reschedule").assertIsNotEnabled()
        compose.onNodeWithText("Time (HH:mm)").performTextReplacement("08:45")
        closeSoftKeyboard()
        compose.onNodeWithText("Reschedule").performClick()
        compose.waitUntil(10000) {runBlocking {store.read().days.first().tasks.first().time=="08:45"}}
        click("A glass of water");compose.onNodeWithText("Skip").performClick()
        compose.waitUntil(10000) {runBlocking {store.read().days.first().tasks.first().status==Status.SKIPPED}}
        click("A glass of water");compose.onNodeWithText("Reset to pending").performClick()
        compose.waitUntil(10000) {runBlocking {store.read().days.first().tasks.first().status==Status.PENDING}}
    }
}
