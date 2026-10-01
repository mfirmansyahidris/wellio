package com.fi.wellio.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable fun Eyebrow(text: String) { Text(text.uppercase(), fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = Forest) }
@Composable fun Heading(title: String, subtitle: String) {
    Text(title, fontSize = 32.sp, lineHeight = 37.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-1).sp)
    Spacer(Modifier.height(8.dp)); Text(subtitle, color = Ink.copy(alpha = .68f), lineHeight = 23.sp)
}
@Composable fun SoftCard(color: Color = Color.White, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(color, RoundedCornerShape(24.dp)).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
}
@Composable fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 54.dp), enabled = enabled, shape = RoundedCornerShape(18.dp)) { Text(text, fontSize = 16.sp) }
}
@Composable fun GardenArt(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(185.dp)) {
        val w = size.width; val h = size.height
        drawCircle(Lime, h * .47f, Offset(w * .53f, h * .5f))
        drawCircle(Peach, h * .22f, Offset(w * .8f, h * .24f))
        drawArc(Forest.copy(alpha=.16f), 180f, 180f, false, Offset(w*.18f,h*.32f), Size(w*.6f,h*1.1f), style=Stroke(2.dp.toPx()))
        drawLine(Forest, Offset(w*.5f,h*.9f), Offset(w*.53f,h*.19f), 4.dp.toPx())
        drawOval(Forest, Offset(w*.34f,h*.35f), Size(w*.18f,h*.2f))
        drawOval(Forest.copy(alpha=.7f), Offset(w*.52f,h*.22f), Size(w*.19f,h*.2f))
        drawOval(Forest.copy(alpha=.85f), Offset(w*.38f,h*.62f), Size(w*.14f,h*.17f))
        drawOval(Forest.copy(alpha=.55f), Offset(w*.51f,h*.53f), Size(w*.17f,h*.19f))
    }
}
