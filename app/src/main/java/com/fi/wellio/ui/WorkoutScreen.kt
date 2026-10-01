package com.fi.wellio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

@Composable fun WorkoutScreen(day: DayPlan, busy: Boolean, selectDate: (String)->Unit, completeSet: (Int)->Unit, finish: ()->Unit, replace: (String)->Unit) {
    val workout=day.workout
    var session by rememberSaveable(day.date) { mutableStateOf(false) }
    var replaceDialog by rememberSaveable { mutableStateOf(false) }
    val total=workout.exercises.sumOf { it.sets }
    val completed=day.session?.completedSets ?: 0
    val done=day.tasks.any { it.kind==TaskKind.WORKOUT && it.status==Status.COMPLETED }
    val future=LocalDate.parse(day.date).isAfter(LocalDate.now())
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement=Arrangement.spacedBy(22.dp)) {
        Eyebrow("Move with intention")
        Heading("A little movement.\nA lift for your day.", "Find your own rhythm. Rest whenever you need to.")
        DateStrip(day.date) {session=false;selectDate(it)}
        SoftCard(Lime) {
            Icon(Icons.Outlined.FitnessCenter, null, Modifier.size(46.dp), tint=Forest)
            Text(workout.title, style=MaterialTheme.typography.headlineMedium, fontWeight=FontWeight.SemiBold)
            Text("${workout.minutes} min • ${workout.level} • ${workout.location}")
            Text(workout.equipment.ifEmpty { listOf("No equipment") }.joinToString())
            Text(workout.description)
            if(done) Text("✓ Workout complete. A little win, just for you.", fontWeight=FontWeight.SemiBold)
            else if(!session) PrimaryButton(if(completed>0) "Continue workout · $completed/$total sets" else "Start workout", enabled=!busy && !future) { session=true }
        }
        if(session && !done) {
            if(completed >= total) {
                SoftCard(Peach) {
                    Text("You made time for you!", style=MaterialTheme.typography.headlineSmall)
                    Text("${workout.exercises.size} exercises · $total sets completed")
                    PrimaryButton("Finish workout", enabled=!busy) {finish();session=false}
                }
            } else {
                val sets=workout.exercises.flatMap { exercise -> (1..exercise.sets).map { exercise to it } }
                var cursor by rememberSaveable(day.date, workout.title, completed) { mutableIntStateOf(completed) }
                val (exercise, set)=sets[cursor.coerceIn(0, total-1)]
                SoftCard {
                    Eyebrow("Set ${cursor+1} of $total")
                    LinearProgressIndicator(progress={completed.toFloat()/total}, modifier=Modifier.fillMaxWidth())
                    Text(exercise.name, style=MaterialTheme.typography.headlineMedium)
                    Text("Set $set of ${exercise.sets}", color=Forest)
                    Text(if(exercise.seconds>0) "${exercise.seconds} SEC" else "${exercise.reps} REPS", fontSize=38.sp, fontWeight=FontWeight.SemiBold)
                    Text(exercise.instruction)
                    Text("Rest ${exercise.restSeconds} seconds between sets. Take longer if you need it.", style=MaterialTheme.typography.bodySmall)
                    PrimaryButton(if(cursor<completed) "Set already completed" else "Complete set", enabled=!busy && cursor==completed) {completeSet(completed)}
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                        TextButton({cursor--}, enabled=cursor>0) {Text("Previous")}
                        TextButton({cursor++}, enabled=cursor<completed) {Text("Next")}
                    }
                    TextButton({session=false}) {Text("Pause & return later")}
                }
            }
        } else {
            Eyebrow("Your movements")
            workout.exercises.forEachIndexed { i, exercise ->
                SoftCard {
                    Row(horizontalArrangement=Arrangement.spacedBy(14.dp), verticalAlignment=Alignment.CenterVertically) {
                        Text("%02d".format(i+1), color=Forest, fontSize=22.sp)
                        Column { Text(exercise.name, fontWeight=FontWeight.SemiBold); Text("${exercise.sets} × ${if(exercise.seconds>0) "${exercise.seconds} sec" else "${exercise.reps} reps"}", style=MaterialTheme.typography.bodySmall) }
                    }
                    Text(exercise.instruction, style=MaterialTheme.typography.bodyMedium)
                }
            }
        }
        if(!done) OutlinedButton({replaceDialog=true}, enabled=!busy, modifier=Modifier.fillMaxWidth()) {Text("Replace workout")}
    }
    if(replaceDialog) ReasonDialog("A different kind of movement?", "Choose a reason. Replacing clears the sets for this workout.", listOf("Too difficult", "Too easy", "Don't have the equipment", "Don't like these exercises", "Need something shorter"), {replaceDialog=false}) {
        replace(it);session=false;replaceDialog=false
    }
}

@Composable fun ReasonDialog(title: String, subtitle: String, reasons: List<String>, dismiss: ()->Unit, choose: (String)->Unit) {
    AlertDialog(onDismissRequest=dismiss, title={Text(title)}, text={Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text(subtitle)
        reasons.forEach { reason -> OutlinedButton({choose(reason)}, modifier=Modifier.fillMaxWidth()) {Text(reason)} }
    }}, confirmButton={TextButton(dismiss){Text("Cancel")}})
}
