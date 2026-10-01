package com.wellio.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wellio.app.WellioViewModel
import com.wellio.app.domain.*
import java.time.LocalDate
import kotlinx.coroutines.delay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable fun WellioApp(vm: WellioViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    var editing by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var taskId by rememberSaveable { mutableStateOf<String?>(null) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.ensureDays(); vm.refreshReminders() }
    LaunchedEffect(state?.profile != null) {
        if (state?.profile != null) {
            vm.ensureDays()
            while(true) { delay(60_000); vm.ensureDays() }
        }
    }
    val snack = remember { SnackbarHostState() }
    LaunchedEffect(message) { message?.let { snack.showSnackbar(it); vm.dismissMessage() } }
    Scaffold(snackbarHost={SnackbarHost(snack)}, bottomBar={
        if (state?.profile != null && !editing) NavigationBar(containerColor=MaterialTheme.colorScheme.background) {
            val titles=listOf("Today", "Workout", "Meals", "Profile")
            val icons=listOf(Icons.Outlined.WbSunny, Icons.Outlined.FitnessCenter, Icons.Outlined.Restaurant, Icons.Outlined.Person)
            titles.forEachIndexed { i, title -> NavigationBarItem(selected=tab==i, onClick={tab=i}, icon={Icon(icons[i], title)}, label={Text(title)}) }
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val current = state
            if (current == null) CircularProgressIndicator(Modifier.padding(32.dp))
            else if (current.profile == null || editing) Onboarding(initial=current.profile ?: com.wellio.app.domain.UserProfile(), editing=editing, busy=busy,
                save={ vm.saveProfile(it) { editing=false } }, cancel={editing=false})
            else {
                val day = current.days.find { it.date == selectedDate } ?: current.days.lastOrNull()
                if (day != null) when(tab) {
                    0 -> TodayScreen(current, day, {selectedDate=it}, {tab=1}, {tab=2}, {taskId=it.id})
                    1 -> WorkoutScreen(day, busy, {selectedDate=it}, {vm.completeSet(day.date,it)}, {vm.finishWorkout(day.date)}, {vm.replaceWorkout(day.date,it)})
                    2 -> MealsScreen(day, busy, {selectedDate=it}, {kind,reason->vm.replaceMeal(day.date,kind,reason)}, {vm.setTask(day.date,it.id,Status.COMPLETED)})
                    3 -> ProfileScreen(current, {editing=true})
                }
                day?.tasks?.find { it.id == taskId }?.let { task ->
                    TaskDialog(task, LocalDate.parse(day.date).isAfter(LocalDate.now()), {taskId=null}, {vm.setReminder(day.date, task.id, it)}) { status, time ->
                        vm.setTask(day.date, task.id, status, time); taskId=null
                    }
                }
            }
        }
    }
    LaunchedEffect(state?.profile) { if (state?.profile != null) editing=false }
}
