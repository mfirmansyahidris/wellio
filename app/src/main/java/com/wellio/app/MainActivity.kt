package com.wellio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.wellio.app.ui.WellioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { WellioTheme { Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding()) { Text("Wellio", style = MaterialTheme.typography.displayLarge); Text("Small steps. A healthier you.") }
        } } }
    }
}
