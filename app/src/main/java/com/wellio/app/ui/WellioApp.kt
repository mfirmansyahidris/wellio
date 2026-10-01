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

@Composable fun WellioApp(vm: WellioViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    var editing by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
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
            else Column(Modifier.padding(24.dp), verticalArrangement=Arrangement.spacedBy(20.dp)) {
                Heading("Hello, ${current.profile.name.ifBlank { "you" }}.", "Your seven-day plan is ready.")
                SoftCard(Lime) { Text("${current.days.size} days of small wins"); Text("${current.profile.minutes} minutes of movement, meals, and space to unwind.") }
                PrimaryButton("Edit preferences", onClick={editing=true})
            }
        }
    }
    LaunchedEffect(state?.profile) { if (state?.profile != null) editing=false }
}
