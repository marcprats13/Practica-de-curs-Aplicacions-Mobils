package com.example.tripletriad.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.tripletriad.model.Player
import com.example.tripletriad.R
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    HelpScreen(onBack = { finish() })
                }
            }
        }
    }
}

data class HelpSection(val icon: String, val title: String, val body: String)

@Composable
fun helpSections(): List<HelpSection> = listOf(
    HelpSection(stringResource(R.string.help_section1_icon), stringResource(R.string.help_section1_title), stringResource(
        R.string.help_section1_body)),
    HelpSection(stringResource(R.string.help_section2_icon), stringResource(R.string.help_section2_title), stringResource(
        R.string.help_section2_body)),
    HelpSection(stringResource(R.string.help_section3_icon), stringResource(R.string.help_section3_title), stringResource(
        R.string.help_section3_body)),
    HelpSection(stringResource(R.string.help_section4_icon), stringResource(R.string.help_section4_title), stringResource(
        R.string.help_section4_body)),
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        MenuBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Capçalera ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -60 }
            ) {
                HelpHeader(onBack = onBack)
            }

            // ── Contingut ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 200))
                ) { CardDemoSection() }

                helpSections().forEachIndexed { index, section ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, delayMillis = 200 + index * 100)) +
                                slideInHorizontally(tween(600, delayMillis = 200 + index * 100)) { 40 }
                    ) { HelpSectionCard(section = section) }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HelpHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(TtBgSurface, TtBgSurface.copy(alpha = 0f))))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .border(1.dp, TtBorder, RoundedCornerShape(4.dp))
                .background(TtBgCard, RoundedCornerShape(4.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onBack)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(stringResource(R.string.help_back), color = TtTextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
        }

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.help_title), fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp, color = TtTextPrimary)
            Text(stringResource(R.string.help_subtitle), fontSize = 9.sp, letterSpacing = 3.sp, color = TtGold)
        }
    }
    HorizontalDivider(color = TtBorder, thickness = 1.dp)
}

@Composable
fun CardDemoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(8.dp))
            .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.help_card_anatomy), fontSize = 10.sp, letterSpacing = 3.sp, color = TtGold, fontWeight = FontWeight.SemiBold)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            CardDemo(top = 7, bottom = 4, left = 5, right = 8, owner = Player.PLAYER_1)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardLegendItem(stringResource(R.string.card_legend_top),    7, TtBlueLight)
                CardLegendItem(stringResource(R.string.card_legend_left),   5, TtBlueLight)
                CardLegendItem(stringResource(R.string.card_legend_right),  8, TtBlueLight)
                CardLegendItem(stringResource(R.string.card_legend_bottom), 4, TtBlueLight)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            PlayerColorChip(TtPlayerBlue,   stringResource(R.string.help_label_player))
            PlayerColorChip(TtOpponentRed,  stringResource(R.string.help_label_opponent))
        }
    }
}

@Composable
fun CardLegendItem(direction: String, value: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(direction, color = TtTextSecondary, fontSize = 11.sp)
        Text("$value", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PlayerColorChip(color: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, color = TtTextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun CardDemo(top: Int, bottom: Int, left: Int, right: Int, owner: Player) {
    val cardColor = when (owner) {
        Player.PLAYER_1 -> TtPlayerBlue
        Player.OPPONENT -> TtOpponentRed
        else            -> TtTextDim
    }
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 96.dp)
            .border(1.5.dp, cardColor, RoundedCornerShape(6.dp))
            .background(Brush.verticalGradient(listOf(cardColor.copy(alpha = 0.2f), TtBgDeep)), RoundedCornerShape(6.dp))
    ) {
        Text("$top",    color = cardColor, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp))
        Text("$bottom", color = cardColor, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp))
        Text("$left",   color = cardColor, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp))
        Text("$right",  color = cardColor, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp))
        Box(modifier = Modifier.size(28.dp).align(Alignment.Center).border(1.dp, cardColor.copy(alpha = 0.3f), RoundedCornerShape(3.dp)).background(cardColor.copy(alpha = 0.08f), RoundedCornerShape(3.dp)))
    }
}

@Composable
fun HelpSectionCard(section: HelpSection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
            .background(TtBgSurface.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, TtGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .background(TtGold.copy(alpha = 0.08f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(section.icon, color = TtGold, fontSize = 16.sp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            Text(section.title, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = TtGoldLight)
            Text(section.body,  fontSize = 13.sp, lineHeight = 20.sp, color = TtTextSecondary)
        }
    }
}