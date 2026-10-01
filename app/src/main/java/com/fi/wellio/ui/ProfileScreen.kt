package com.fi.wellio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fi.wellio.domain.*
import java.time.LocalDate
import java.time.DayOfWeek

@Composable fun ProfileScreen(state: WellnessState, edit: ()->Unit, extra: @Composable ColumnScope.()->Unit = {}) {
    val profile = state.profile ?: return
    val today=LocalDate.now()
    val monday=today.with(DayOfWeek.MONDAY)
    val week=state.days.filter { LocalDate.parse(it.date) in monday..today }
    val tasks=week.flatMap { it.tasks }
    val workouts=tasks.count { it.kind==TaskKind.WORKOUT && it.status==Status.COMPLETED }
    val percentage=if(tasks.isEmpty()) 0 else tasks.count { it.status==Status.COMPLETED }*100/tasks.size
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement=Arrangement.spacedBy(24.dp)) {
        Eyebrow("Your space")
        Heading("Growing, at\nyour own pace.", "${profile.name.ifBlank { "Your" }}${if(profile.name.isBlank()) "" else "'s"} week of little wins.")
        SoftCard(Lime) {
            Text("This week", style=MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                (0..6).forEach { index ->
                    val date=monday.plusDays(index.toLong())
                    val done=state.days.find { it.date==date.toString() }?.tasks?.any { it.kind==TaskKind.WORKOUT && it.status==Status.COMPLETED }==true
                    Column(horizontalAlignment=Alignment.CenterHorizontally) { Text(listOf("M","T","W","T","F","S","S")[index], fontSize=12.sp); Spacer(Modifier.height(8.dp)); Text(if(done) "✓" else "·", fontSize=24.sp, color=Forest) }
                }
            }
            Text("$workouts workouts completed", fontWeight=FontWeight.SemiBold)
        }
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Surface(Modifier.weight(1f), color=Peach, shape=RoundedCornerShape(24.dp)) { Column(Modifier.padding(20.dp)) { Text("$percentage%", fontSize=32.sp); Text("Routine completion", fontSize=12.sp) } }
            Surface(Modifier.weight(1f), color=MaterialTheme.colorScheme.surfaceVariant, shape=RoundedCornerShape(24.dp)) { Column(Modifier.padding(20.dp)) { Text("${streak(state.days,today)} days", fontSize=32.sp); Text("Current streak", fontSize=12.sp) } }
        }
        Text("A streak means completing at least one activity each day. Skipped activities don't count as completed.", style=MaterialTheme.typography.bodySmall)
        SoftCard {
            Text("Made for you", style=MaterialTheme.typography.titleLarge)
            Text(profile.goals.joinToString(" · ") { it.label })
            Text("${profile.level} · ${profile.minutes} minutes · ${profile.location}")
            Text("${profile.diet} · ${profile.cuisine}")
            if(profile.avoid.isNotEmpty()) Text("Avoiding: ${profile.avoid.joinToString()}")
            PrimaryButton("Edit preferences", onClick=edit)
            Text("Saving preferences refreshes pending activities. Completed activities and your history stay with you.", style=MaterialTheme.typography.bodySmall)
        }
        extra()
        Text("Wellio 0.1 · Built around your everyday\nNo account. No calorie counting. Just small steps.", style=MaterialTheme.typography.bodySmall, color=Ink.copy(alpha=.65f))
    }
}
