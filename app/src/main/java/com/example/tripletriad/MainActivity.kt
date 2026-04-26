package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    MainMenuScreen(
                        onHelp     = { startActivity(Intent(this, HelpActivity::class.java)) },
                        onStartGame = { startActivity(Intent(this, ConfigurationActivity::class.java)) },
                        onExit     = { finishAffinity() }
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    onHelp: () -> Unit,
    onStartGame: () -> Unit,
    onExit: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); visible = true }

    val infiniteTransition = rememberInfiniteTransition(label = "title_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        MenuBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Títol ────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800, easing = EaseOutCubic)) { -80 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalDividerWithDiamonds()
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.menu_title_top),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 12.sp,
                        color = TtTextPrimary,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(color = TtBluePrimary.copy(alpha = glowAlpha), blurRadius = 24f)
                        )
                    )
                    Text(
                        text = stringResource(R.string.menu_title_bottom),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 12.sp,
                        color = TtGoldLight,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(color = TtGold.copy(alpha = glowAlpha), blurRadius = 28f)
                        )
                    )
                    Spacer(Modifier.height(8.dp))

                    HorizontalDividerWithDiamonds()
                }
            }

            Spacer(Modifier.height(56.dp))

            // ── Botons ───────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 300)) +
                        slideInVertically(tween(800, delayMillis = 300, easing = EaseOutCubic)) { 60 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuButton(label = stringResource(R.string.menu_new_game), icon = "▶", isPrimary = true,  onClick = onStartGame)
                    MenuButton(label = stringResource(R.string.menu_help),     icon = "?", isPrimary = false, onClick = onHelp)
                    MenuButton(label = stringResource(R.string.menu_exit),     icon = "✕", isPrimary = false, onClick = onExit)
                }
            }

            Spacer(Modifier.height(48.dp))

            // ── Cartes decoratives ───────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(1000, delayMillis = 600))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i -> MiniCardDecoration(delay = i * 150) }
                }
            }
        }
    }
}

// ─── Background ──────────────────────────────────────────────────────────────
@Composable
fun MenuBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
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
        drawGrid(this, offset)
    }
}

private fun drawGrid(scope: DrawScope, animOffset: Float) {
    val spacing = 48f
    val totalCols = (scope.size.width / spacing).toInt() + 2
    val totalRows = (scope.size.height / spacing).toInt() + 2
    val shift = (animOffset * spacing) % spacing
    for (col in 0..totalCols) {
        val x = col * spacing - shift
        scope.drawLine(color = TtBorder, strokeWidth = 0.5f, start = Offset(x, 0f), end = Offset(x, scope.size.height))
    }
    for (row in 0..totalRows) {
        val y = row * spacing - shift
        scope.drawLine(color = TtBorder, strokeWidth = 0.5f, start = Offset(0f, y), end = Offset(scope.size.width, y))
    }
}

// ─── Divider decoratiu ────────────────────────────────────────────────────────
@Composable
fun HorizontalDividerWithDiamonds() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = TtGold.copy(alpha = 0.4f), thickness = 1.dp)
        Text(text = "  ◆  ", color = TtGold, fontSize = 10.sp)
        HorizontalDivider(modifier = Modifier.weight(1f), color = TtGold.copy(alpha = 0.4f), thickness = 1.dp)
    }
}

// ─── Botó de menú ─────────────────────────────────────────────────────────────
@Composable
fun MenuButton(label: String, icon: String, isPrimary: Boolean, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    val borderColor = if (isPrimary) TtGold else TtBorder
    val bgColor     = if (isPrimary) TtBluePrimary.copy(alpha = 0.15f) else TtBgSurface.copy(alpha = 0.6f)
    val textColor   = if (isPrimary) TtGoldLight else TtTextSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .scale(scale)
            .border(width = if (isPrimary) 1.5.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
            .background(bgColor, RoundedCornerShape(4.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                pressed = true; onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, color = borderColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = label, fontSize = 14.sp, letterSpacing = 3.sp, fontWeight = FontWeight.SemiBold, color = textColor)
        }
    }

    LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }
}

// ─── Mini carta decorativa ────────────────────────────────────────────────────
@Composable
fun MiniCardDecoration(delay: Int = 0) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + delay, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "float"
    )
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 56.dp)
            .offset(y = offsetY.dp)
            .border(1.dp, TtBluePrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .background(Brush.verticalGradient(listOf(TtBgCard, TtBgDeep)), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("?", color = TtBlueLight.copy(alpha = 0.4f), fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}