package com.fi.wellio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fi.wellio.domain.UserProfile

@Composable fun AiSettings(profile: UserProfile, configured: Boolean, busy: Boolean, enable: (Boolean)->Unit, generate: ()->Unit) {
    var consent by rememberSaveable { mutableStateOf(false) }
    SoftCard {
        Text("A more personal week", style=MaterialTheme.typography.titleLarge)
        Text(if(configured) "Cloud AI is connected. You choose when to use it." else "You're using local plans. Cloud AI will be available when this app is connected.")
        Row(verticalAlignment=Alignment.CenterVertically) {
            Text("Use cloud AI", Modifier.weight(1f))
            Switch(checked=profile.aiEnabled, enabled=configured && !busy, onCheckedChange={if(it) consent=true else enable(false)})
        }
        if(profile.aiEnabled) {
            Text("Preferences are sent to Google/Firebase when you generate or replace a plan. Your name and completion history stay on this device.", style=MaterialTheme.typography.bodySmall)
            PrimaryButton(if(busy) "Preparing your plan…" else "Generate 7 days with AI", enabled=!busy, onClick=generate)
        }
    }
    if(consent) AlertDialog(onDismissRequest={consent=false}, title={Text("Personalize with cloud AI?")}, text={Text("Your goals, activity level, workout settings and food preferences will be sent to Google/Firebase to generate plans and replacements. Your name and completion history won't be sent. You can turn this off anytime. Existing completed activities are preserved.")}, confirmButton={TextButton({enable(true);consent=false}){Text("Enable AI")}}, dismissButton={TextButton({consent=false}){Text("Keep local plans")}})
}
