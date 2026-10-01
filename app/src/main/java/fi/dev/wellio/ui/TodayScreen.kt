package fi.dev.wellio.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.dev.wellio.domain.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun kindIcon(kind: TaskKind) = when(kind) {
    TaskKind.WORKOUT -> Icons.Outlined.FitnessCenter
    TaskKind.WATER -> Icons.Outlined.WaterDrop
    TaskKind.WALK -> Icons.AutoMirrored.Outlined.DirectionsWalk
    TaskKind.WIND_DOWN -> Icons.Outlined.DarkMode
    else -> Icons.Outlined.Restaurant
}
@Composable fun DateStrip(selected: String, select: (String)->Unit) {
    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        items(7) { offset ->
            val date = LocalDate.now().plusDays(offset.toLong())
            val active = date.toString() == selected
            Surface(onClick={select(date.toString())}, shape=RoundedCornerShape(20.dp), color=if(active) Forest else Color.White) {
                Column(Modifier.width(55.dp).padding(vertical=13.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(date.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)), fontSize=11.sp)
                    Spacer(Modifier.height(7.dp)); Text(date.dayOfMonth.toString(), fontWeight=FontWeight.SemiBold, fontSize=20.sp)
                }
            }
        }
    }
}
@Composable fun TodayScreen(state: WellnessState, day: DayPlan, selectDate: (String)->Unit, openWorkout: ()->Unit, openMeals: ()->Unit, taskClick: (DailyTask)->Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement=Arrangement.spacedBy(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
            Text("wellio", fontSize=27.sp, letterSpacing=(-1).sp, fontWeight=FontWeight.Bold, color=Forest)
            Surface(color=Lime, shape=RoundedCornerShape(50)) { Text("✦  ${streak(state.days, LocalDate.now())} day streak", Modifier.padding(horizontal=12.dp, vertical=8.dp), fontSize=12.sp) }
        }
        Column { Eyebrow(LocalDate.parse(day.date).format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))); Spacer(Modifier.height(10.dp))
            Heading("Small steps.\nGood things ahead.", "Hello${state.profile?.name?.takeIf { it.isNotBlank() }?.let { ", $it" } ?: ""}. Make a little time for you.") }
        DateStrip(day.date, selectDate)
        Surface(color=Forest, shape=RoundedCornerShape(26.dp)) {
            Row(Modifier.fillMaxWidth().padding(22.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(20.dp)) {
                Box(Modifier.size(90.dp), contentAlignment=Alignment.Center) {
                    Canvas(Modifier.fillMaxSize().padding(6.dp)) {
                        drawArc(Color.White.copy(alpha=.18f), -90f, 360f, false, style=Stroke(7.dp.toPx()))
                        drawArc(Lime, -90f, day.percent()*3.6f, false, style=Stroke(7.dp.toPx(), cap=androidx.compose.ui.graphics.StrokeCap.Round))
                    }
                    Text("${day.percent()}%", fontSize=24.sp, fontWeight=FontWeight.SemiBold, color=Color.White)
                }
                Column { Text("Your daily rhythm", color=Color.White, fontWeight=FontWeight.SemiBold, fontSize=20.sp)
                    Spacer(Modifier.height(8.dp)); Text("${day.completed()} of ${day.tasks.size} little wins", color=Color.White.copy(alpha=.8f))
                    Spacer(Modifier.height(5.dp)); Text("Every small step counts.", fontSize=12.sp, color=Lime) }
            }
        }
        Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Eyebrow("Make room for movement")
            Surface(onClick=openWorkout, color=Lime, shape=RoundedCornerShape(24.dp)) {
                Row(Modifier.fillMaxWidth().padding(22.dp), verticalAlignment=Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(day.workout.title, style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.SemiBold)
                        Spacer(Modifier.height(10.dp)); Text("${day.workout.minutes} min • ${day.workout.level}", fontSize=13.sp)
                        Spacer(Modifier.height(14.dp)); Text("View your workout  →", fontWeight=FontWeight.SemiBold, color=Forest) }
                    Icon(Icons.Outlined.FitnessCenter, null, Modifier.size(52.dp), tint=Forest)
                }
            }
        }
        Surface(onClick=openMeals, color=Peach, shape=RoundedCornerShape(24.dp)) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Outlined.Restaurant, null, Modifier.size(32.dp), tint=Forest)
                Column(Modifier.weight(1f).padding(horizontal=16.dp)) { Text("Nourish your day", style=MaterialTheme.typography.titleLarge); Text("Four simple meal ideas", fontSize=13.sp) }
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, "View meals")
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) { Eyebrow("Your daily routine"); Text(day.source, fontSize=11.sp, color=Forest) }
        Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
            day.tasks.sortedBy { it.time }.forEach { task ->
                Surface(onClick={taskClick(task)}, shape=RoundedCornerShape(20.dp), color=Color.White) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                        Icon(if(task.status==Status.COMPLETED) Icons.Outlined.CheckCircle else kindIcon(task.kind), null, tint=Forest)
                        Column(Modifier.weight(1f)) { Text(task.title, fontWeight=FontWeight.Medium); Text("${task.time} · ${task.status.name.lowercase().replaceFirstChar { it.uppercase() }}${if(task.reminder) " · Reminder on" else ""}", fontSize=12.sp, color=Ink.copy(alpha=.6f)) }
                        Icon(Icons.Outlined.ChevronRight, "Manage ${task.title}", tint=Forest)
                    }
                }
            }
        }
        Text("You don't have to do it all. Just take the next small step.", fontSize=13.sp, color=Ink.copy(alpha=.65f))
    }
}

@Composable fun TaskDialog(task: DailyTask, isFuture: Boolean, dismiss: ()->Unit, reminder: (Boolean)->Unit, update: (Status, String?)->Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val permission = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { granted -> reminder(granted) }
    var time by rememberSaveable(task.id) { mutableStateOf(task.time) }
    val valid = Regex("([01][0-9]|2[0-3]):[0-5][0-9]").matches(time)
    AlertDialog(onDismissRequest=dismiss, title={Text(task.title)}, text={
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("${task.time} · ${task.status.name.lowercase()}")
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Button({update(Status.COMPLETED, null)}, enabled=!isFuture) { Text("Done") }
                OutlinedButton({update(Status.SKIPPED, null)}, enabled=!isFuture) { Text("Skip") }
            }
            OutlinedTextField(time, {time=it}, label={Text("Time (HH:mm)")}, singleLine=true, isError=!valid, modifier=Modifier.fillMaxWidth())
            OutlinedButton({update(Status.RESCHEDULED, time)}, enabled=valid) { Text("Reschedule") }
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("Remind me", Modifier.weight(1f))
                Switch(checked=task.reminder, onCheckedChange={ enabled ->
                    if(enabled && android.os.Build.VERSION.SDK_INT >= 33 && androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                        permission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    else reminder(enabled)
                })
            }
            Text("Reminders are approximate and may be delayed by battery settings. Past times won't trigger a notification.", style=MaterialTheme.typography.bodySmall)
            if(!androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                TextButton({ context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)) }) { Text("Allow notifications in settings") }
            }
            if(task.status != Status.PENDING) TextButton({update(Status.PENDING, null)}) { Text("Reset to pending") }
        }
    }, confirmButton={TextButton(dismiss){Text("Close")}})
}
