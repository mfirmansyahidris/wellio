package com.fi.wellio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fi.wellio.domain.*
import com.fi.wellio.data.stateJson
import kotlinx.serialization.encodeToString

@OptIn(ExperimentalLayoutApi::class)
@Composable fun ChoiceGroup(title: String, options: List<String>, selected: List<String>, onSelect: (String) -> Unit) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        options.forEach { value -> FilterChip(selected = value in selected, onClick = { onSelect(value) }, label = { Text(value) }) }
    }
}
@Composable fun Onboarding(initial: UserProfile = UserProfile(), editing: Boolean = false, busy: Boolean, save: (UserProfile) -> Unit, cancel: () -> Unit = {}) {
    var encoded by rememberSaveable { mutableStateOf(stateJson.encodeToString(initial)) }
    val p = remember(encoded) { stateJson.decodeFromString<UserProfile>(encoded) }
    fun update(next: UserProfile) { encoded = stateJson.encodeToString(next) }
    var step by rememberSaveable { mutableIntStateOf(if (editing) 1 else 0) }
    var avoids by rememberSaveable { mutableStateOf(initial.avoid.joinToString(", ")) }
    val scroll = rememberScrollState()
    LaunchedEffect(step) { scroll.scrollTo(0) }
    Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Eyebrow("wellio / your daily companion")
            if (editing) TextButton(cancel) { Text("Close") }
        }
        if (step > 0) {
            LinearProgressIndicator(progress = { step / 4f }, modifier = Modifier.fillMaxWidth(), color = Forest, trackColor = Lime)
            Text("STEP $step OF 4", style = MaterialTheme.typography.labelMedium)
        }
        when (step) {
            0 -> {
                GardenArt()
                Heading("A little better,\nevery day.", "Build a healthier routine that fits your life.")
                SoftCard(Lime) {
                    Text("Your day, thoughtfully planned", style = MaterialTheme.typography.titleLarge)
                    Text("A little movement. Food you enjoy. Time to reset. Let's make space for all three.")
                }
                Text("No account needed. Your progress stays on this device.", style = MaterialTheme.typography.bodySmall)
                PrimaryButton("Let's get started", onClick = { step = 1 })
                Text("Wellio supports everyday wellness. It doesn't diagnose or treat health conditions.", style = MaterialTheme.typography.bodySmall, color = Ink.copy(alpha=.6f))
            }
            1 -> {
                Heading("What feels right\nfor you?", "Choose up to two things you'd like to work on.")
                OutlinedTextField(p.name, { if (it.length <= 40) update(p.copy(name = it)) }, label = { Text("Your first name (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Goal.entries.forEach { goal ->
                    FilterChip(selected = goal in p.goals, onClick = {
                        val next = if (goal in p.goals) p.goals - goal else if (p.goals.size < 2) p.goals + goal else p.goals
                        update(p.copy(goals = next))
                    }, label = { Text(goal.label, Modifier.padding(vertical = 8.dp)) }, modifier = Modifier.fillMaxWidth())
                }
            }
            2 -> {
                Heading("Movement,\non your terms.", "We'll start where you are, with the time you have.")
                ChoiceGroup("Your everyday activity", listOf("Mostly sitting", "Lightly active", "Active", "Very active"), listOf(p.activity)) { update(p.copy(activity=it)) }
                ChoiceGroup("Workout experience", listOf("Beginner", "Intermediate"), listOf(p.level)) { update(p.copy(level=it)) }
                ChoiceGroup("Time for movement", listOf(10,20,30,45,60).map { "$it min" }, listOf("${p.minutes} min")) { update(p.copy(minutes=it.substringBefore(" ").toInt())) }
                ChoiceGroup("Your space", listOf("Home", "Gym", "Outdoor"), listOf(p.location)) { update(p.copy(location=it)) }
                ChoiceGroup("Available equipment", listOf("No equipment", "Dumbbells", "Resistance bands", "Bench", "Barbell", "Treadmill", "Exercise bike"), p.equipment.ifEmpty { listOf("No equipment") }) {
                    update(p.copy(equipment = if (it == "No equipment") emptyList() else if (it in p.equipment) p.equipment-it else p.equipment+it))
                }
            }
            3 -> {
                Heading("Good food.\nYour kind of food.", "Simple ideas to make mealtimes a little easier.")
                ChoiceGroup("Dietary preference", listOf("Regular", "Vegetarian", "Vegan", "High Protein", "Low Carb"), listOf(p.diet)) { update(p.copy(diet=it)) }
                ChoiceGroup("Favorite cuisine", listOf("Anything", "Indonesian", "Asian", "Western", "Japanese", "Korean"), listOf(p.cuisine)) { update(p.copy(cuisine=it)) }
                ChoiceGroup("What matters to you?", listOf("Simple cooking", "Budget friendly", "High protein", "Quick preparation", "Balanced", "Lots of vegetables"), p.priorities) { update(p.copy(priorities = if (it in p.priorities) p.priorities-it else p.priorities+it)) }
                OutlinedTextField(avoids, { if (it.length <= 400) avoids=it }, modifier=Modifier.fillMaxWidth(), label={Text("Foods to avoid")}, supportingText={Text("Separate with commas: milk, peanuts, seafood. Food preferences only; ingredient lists are not an allergy guarantee.")})
            }
            4 -> {
                GardenArt(Modifier.height(140.dp))
                Heading("A week of\nsmall wins.", "Your first seven days, ready when you are.")
                SoftCard(Lime) {
                    Text(p.goals.joinToString(" + ") { it.label }, style=MaterialTheme.typography.titleLarge)
                    Text("${p.minutes} min • ${p.level} • ${p.location}")
                    Text("${p.diet} meals • ${p.cuisine} cuisine")
                    Text(if(p.aiEnabled) "Refresh your connected AI plan using these preferences. Completed activities stay in your history." else "Start with a personalized local plan. Cloud AI can be enabled later when connected.", style=MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (step > 0) {
            PrimaryButton(if (busy) "Preparing your week…" else if (step == 4) if (editing) "Save & refresh my plan" else "Create my plan" else "Continue", enabled = !busy && p.goals.isNotEmpty()) {
                if (step == 4) save(p.copy(avoid = avoids.split(',').map { it.trim() }.filter { it.isNotEmpty() }.distinct())) else step++
            }
            TextButton({ step-- }, enabled=!busy, modifier=Modifier.fillMaxWidth()) { Text("Back") }
        }
        Spacer(Modifier.height(12.dp))
    }
}
