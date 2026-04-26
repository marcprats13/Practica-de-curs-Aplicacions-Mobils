package com.example.tripletriad.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripletriad.ui.theme.*

@Composable
fun MenuBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF0D1B3E), TtBgDeep),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.height * 0.75f
            )
        )
        drawAnimatedGrid(offset)  // <-- sense "this"
    }
}

fun DrawScope.drawAnimatedGrid(animOffset: Float) {  // <-- extensió de DrawScope
    val spacing = 48f
    val totalCols = (size.width / spacing).toInt() + 2
    val totalRows = (size.height / spacing).toInt() + 2
    val shift = (animOffset * spacing) % spacing
    for (col in 0..totalCols) {
        val x = col * spacing - shift
        drawLine(
            color = TtBorder, strokeWidth = 0.5f,
            start = Offset(x, 0f), end = Offset(x, size.height)
        )
    }
    for (row in 0..totalRows) {
        val y = row * spacing - shift
        drawLine(
            color = TtBorder, strokeWidth = 0.5f,
            start = Offset(0f, y), end = Offset(size.width, y)
        )
    }
}

@Composable
fun HorizontalDividerWithDiamonds() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = TtGold.copy(alpha = 0.4f),
            thickness = 1.dp
        )
        Text(text = "  ◆  ", color = TtGold, fontSize = 10.sp)
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = TtGold.copy(alpha = 0.4f),
            thickness = 1.dp
        )
    }
}