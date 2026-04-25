package com.example.tripletriad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.tripletriad.ui.theme.TripleTriadTheme
import kotlinx.coroutines.delay

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF070B14)
                ) {
                    HelpScreen(onBack = { finish() })
                }
            }
        }
    }
}

// ─── Colors (mateixos que MainActivity) ──────────────────────────────────────
private val BgDeep        = Color(0xFF070B14)
private val BgSurface     = Color(0xFF0D1526)
private val BgCard        = Color(0xFF111C35)
private val BluePrimary   = Color(0xFF1A6FD4)
private val BlueLight     = Color(0xFF4D9FFF)
private val GoldColor     = Color(0xFFC8963E)
private val GoldLight     = Color(0xFFF0C060)
private val TextPrimary   = Color(0xFFEEF2FF)
private val TextSecondary = Color(0xFF8899BB)
private val TextDim       = Color(0xFF445577)
private val BorderColor   = Color(0xFF1E2D4D)
private val PlayerBlue    = Color(0xFF1A6FD4)
private val OpponentRed   = Color(0xFFC0392B)

// ─── Dades de les seccions d'ajuda ───────────────────────────────────────────
data class HelpSection(
    val icon: String,
    val title: String,
    val body: String
)

private val helpSections = listOf(
    HelpSection(
        icon = "⊞",
        title = "EL TABLERO",
        body = "El juego se desenvolupa en un tablero de 3×3 casillas. " +
                "Los dos jugadores empiezan con 5 cartas cada uno. " +
                "El tablero se llena completamente al final de la partida."
    ),
    HelpSection(
        icon = "◈",
        title = "LAS CARTAS",
        body = "Cada carta tien 4 valores numèricos (1–9) a los lados: " +
                "Superior, Inferior, Izquierda i Derecha. " +
                "El valor indica la fuerza de ese lado en combate."
    ),
    HelpSection(
        icon = "⚔",
        title = "CAPTURAS",
        body = "Al colocar una carta, se compara con las cartas adyacentes del rival. " +
                "Si tu valor es MAYOR que el del lado opuesto del rival, capturas su carta " +
                "y cambia a tu color."
    ),
    HelpSection(
        icon = "◆",
        title = "VICTORIA",
        body = "Cuando el tablero se llena, gana el jugador con más cartas de su color. " +
                "Se cuentan todas las cartas en el tablero, sean originales o capturadas."
    ),

)

// ─── Help Screen ──────────────────────────────────────────────────────────────
@Composable
fun HelpScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fons
        MenuBackground()

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Capçalera ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -60 }
            ) {
                HelpHeader(onBack = onBack)
            }

            // ── Contingut scrollable ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Demo de carta
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 200))
                ) {
                    CardDemoSection()
                }

                // Seccions d'ajuda
                helpSections.forEachIndexed { index, section ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, delayMillis = 200 + index * 100)) +
                                slideInHorizontally(tween(600, delayMillis = 200 + index * 100)) { 40 }
                    ) {
                        HelpSectionCard(section = section)
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ─── Capçalera ────────────────────────────────────────────────────────────────
@Composable
fun HelpHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(BgSurface, BgSurface.copy(alpha = 0.0f))
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Botó enrere
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                .background(BgCard, RoundedCornerShape(4.dp))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = onBack
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text("◀  VOLVER", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
        }

        // Títol centrat
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AYUDA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = TextPrimary
            )
            Text(
                text = "TRIPLE TRIAD",
                fontSize = 9.sp,
                letterSpacing = 3.sp,
                color = GoldColor
            )
        }
    }

    HorizontalDivider(color = BorderColor, thickness = 1.dp)
}

// ─── Demo de carta visual ──────────────────────────────────────────────────────
@Composable
fun CardDemoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .background(BgSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ANATOMIA DE LA CARTA",
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            color = GoldColor,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Carta de demostració
            CardDemo(top = 7, bottom = 4, left = 5, right = 8, owner = Player.PLAYER_1)

            // Llegenda
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardLegendItem(direction = "▲ Superior", value = 7, color = BlueLight)
                CardLegendItem(direction = "◀ Esquerre", value = 5, color = BlueLight)
                CardLegendItem(direction = "▶ Dret",    value = 8, color = BlueLight)
                CardLegendItem(direction = "▼ Inferior", value = 4, color = BlueLight)
            }
        }

        // Colors dels jugadors
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerColorChip(color = PlayerBlue, label = "JUGADOR")
            PlayerColorChip(color = OpponentRed, label = "RIVAL")
        }
    }
}

@Composable
fun CardLegendItem(direction: String, value: Int, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = direction, color = TextSecondary, fontSize = 11.sp)
        Text(
            text = "$value",
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerColorChip(color: Color, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(text = label, color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

// ─── Carta de demostració ─────────────────────────────────────────────────────
@Composable
fun CardDemo(top: Int, bottom: Int, left: Int, right: Int, owner: Player) {
    val cardColor = when (owner) {
        Player.PLAYER_1 -> PlayerBlue
        Player.OPPONENT -> OpponentRed
        else -> Color(0xFF445577)
    }

    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 96.dp)
            .border(1.5.dp, cardColor, RoundedCornerShape(6.dp))
            .background(
                Brush.verticalGradient(listOf(cardColor.copy(alpha = 0.2f), BgDeep)),
                RoundedCornerShape(6.dp)
            )
    ) {
        // Top
        Text(
            text = "$top",
            color = cardColor,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp)
        )
        // Bottom
        Text(
            text = "$bottom",
            color = cardColor,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
        )
        // Left
        Text(
            text = "$left",
            color = cardColor,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp)
        )
        // Right
        Text(
            text = "$right",
            color = cardColor,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp)
        )
        // Centre
        Box(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.Center)
                .border(1.dp, cardColor.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                .background(cardColor.copy(alpha = 0.08f), RoundedCornerShape(3.dp))
        )
    }
}

// ─── Secció d'ajuda individual ────────────────────────────────────────────────
@Composable
fun HelpSectionCard(section: HelpSection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
            .background(BgSurface.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icona
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, GoldColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .background(GoldColor.copy(alpha = 0.08f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = section.icon, color = GoldColor, fontSize = 16.sp)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = section.title,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
            Text(
                text = section.body,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TextSecondary
            )
        }
    }
}