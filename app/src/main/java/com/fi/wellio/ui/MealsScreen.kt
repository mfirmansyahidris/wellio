package com.fi.wellio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fi.wellio.domain.*
import java.time.LocalDate

@Composable fun MealsScreen(day: DayPlan, busy: Boolean, selectDate: (String)->Unit, replace: (TaskKind,String)->Unit, complete: (DailyTask)->Unit) {
    var detailName by rememberSaveable(day.date) { mutableStateOf<String?>(null) }
    var replacing by rememberSaveable(day.date) { mutableStateOf(false) }
    val kind=detailName?.let { TaskKind.valueOf(it) }
    val detail=kind?.let { day.meals[it] }
    val scroll=rememberScrollState()
    LaunchedEffect(detailName) { scroll.scrollTo(0) }
    Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(24.dp), verticalArrangement=Arrangement.spacedBy(22.dp)) {
        if(detail!=null) {
            TextButton({detailName=null}) {Text("← All meals")}
            Eyebrow(kind.name)
            Heading(detail.name, "Simple ingredients. A moment to enjoy.")
            SoftCard(Peach) {
                Icon(Icons.Outlined.Restaurant, null, Modifier.size(44.dp), tint=Forest)
                Text("About ${detail.minutes} min preparation", style=MaterialTheme.typography.titleMedium)
                Text(detail.tags.filter { it !in listOf("Main","Snack","Breakfast") }.joinToString(" · "))
            }
            SoftCard {
                Text("What you'll need", style=MaterialTheme.typography.titleLarge)
                detail.ingredients.forEach { Text("•  $it") }
            }
            Eyebrow("Keep it simple")
            detail.instructions.forEachIndexed { i, step ->
                Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) { Text("%02d".format(i+1), color=Forest, fontSize=22.sp); Text(step, Modifier.weight(1f)) }
            }
            val task=day.tasks.first { it.kind==kind }
            val completed=task.status==Status.COMPLETED
            PrimaryButton(if(completed) "✓ Meal completed" else "Mark as done", enabled=!busy && !completed && !LocalDate.parse(day.date).isAfter(LocalDate.now())) {complete(task)}
            OutlinedButton({replacing=true}, enabled=!busy && !completed, modifier=Modifier.fillMaxWidth()) {Text("Replace meal")}
            Text("Check ingredients and packaging for your needs. Food preferences are not medical allergy screening.", style=MaterialTheme.typography.bodySmall, color=Ink.copy(alpha=.65f))
        } else {
            Eyebrow("Nourish, simply")
            Heading("Good food.\nLess overthinking.", "A few ideas to bring a little ease to your day.")
            DateStrip(day.date, selectDate)
            mealKinds.forEachIndexed { index, type ->
                val meal=day.meals.getValue(type)
                val task=day.tasks.first { it.kind==type }
                Surface(onClick={detailName=type.name}, shape=RoundedCornerShape(24.dp), color=if(index%2==0) Peach else MaterialTheme.colorScheme.surfaceVariant) {
                    Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {Eyebrow(type.name); Text(task.time, fontSize=12.sp)}
                        Icon(when(type){TaskKind.BREAKFAST->Icons.Outlined.WbSunny;TaskKind.SNACK->Icons.Outlined.LocalCafe;else->Icons.Outlined.Restaurant}, null, Modifier.size(36.dp), tint=Forest)
                        Text(meal.name, style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.SemiBold)
                        Text("${meal.minutes} min prep · ${if(task.status==Status.COMPLETED) "Completed ✓" else "View recipe →"}", fontSize=13.sp)
                    }
                }
            }
            Text("Ideas, not rules. Swap a meal whenever your day calls for something different.", style=MaterialTheme.typography.bodySmall)
        }
    }
    if(replacing && kind!=null) ReasonDialog("Something else on the menu?", "Only this meal will change. The rest of your day stays as it is.", listOf("Don't like it", "Ingredients unavailable", "Something cheaper", "Something quicker", "Surprise me"), {replacing=false}) {
        replace(kind,it);replacing=false
    }
}
