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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    ConfiguracionScreen(
                        onStartGame = { alias, isTimeEnabled, isBorders, isReverse ->
                            val intent = Intent(this, GameActivity::class.java).apply {
                                putExtra(IntentKeys.EXTRA_ALIAS,        alias)
                                putExtra(IntentKeys.EXTRA_TIME_CONTROL, isTimeEnabled)
                                putExtra(IntentKeys.EXTRA_BORDERS_MODE, isBorders)
                                putExtra(IntentKeys.EXTRA_REVERSE_MODE, isReverse)
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConfiguracionScreen(onStartGame: (String, Boolean, Boolean, Boolean) -> Unit) {

    var alias         by remember { mutableStateOf("") }
    var isAliasError  by remember { mutableStateOf(false) }
    var isTimeEnabled by remember { mutableStateOf(false) }
    var isBordersMode by remember { mutableStateOf(false) }
    var isReverseMode by remember { mutableStateOf(false) }

    // Animació d'entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        MenuBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // ── Capçalera ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700)) + slideInVertically(tween(700, easing = EaseOutCubic)) { -60 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalDividerWithDiamonds()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.config_title),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                        color = TtTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.config_subtitle),
                        fontSize = 10.sp,
                        letterSpacing = 3.sp,
                        color = TtGold
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDividerWithDiamonds()
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Camp àlies ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, 150)) + slideInHorizontally(tween(700, 150)) { -40 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isAliasError) 1.5.dp else 1.dp,
                            color = if (isAliasError) TtOpponentRed else TtBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.config_alias_label).uppercase(),
                        fontSize = 9.sp,
                        letterSpacing = 3.sp,
                        color = TtGold,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = alias,
                        onValueChange = {
                            alias = it
                            if (it.isNotBlank()) isAliasError = false
                        },
                        placeholder = { Text("ex: Squall", color = TtTextDim, fontSize = 14.sp) },
                        singleLine = true,
                        isError = isAliasError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = TtBluePrimary,
                            unfocusedBorderColor  = TtBorder,
                            errorBorderColor      = TtOpponentRed,
                            focusedTextColor      = TtTextPrimary,
                            unfocusedTextColor    = TtTextPrimary,
                            cursorColor           = TtBlueLight,
                            focusedContainerColor    = TtBgCard,
                            unfocusedContainerColor  = TtBgCard,
                            errorContainerColor      = TtOpponentRed.copy(alpha = 0.05f)
                        )
                    )
                    // Missatge d'error
                    AnimatedVisibility(visible = isAliasError) {
                        Text(
                            text = stringResource(R.string.config_alias_error),
                            fontSize = 11.sp,
                            color = TtOpponentRed,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Opcions de joc ────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, 300)) + slideInVertically(tween(700, 300)) { 40 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
                        .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    ConfigOptionRow(
                        title    = stringResource(R.string.config_time_title),
                        subtitle = stringResource(R.string.config_time_sub),
                        icon     = "⏱",
                        checked  = isTimeEnabled,
                        onCheckedChange = { isTimeEnabled = it },
                        showDivider = true
                    )
                    ConfigOptionRow(
                        title    = stringResource(R.string.config_borders_title),
                        subtitle = stringResource(R.string.config_borders_sub),
                        icon     = "⊕",
                        checked  = isBordersMode,
                        onCheckedChange = { isBordersMode = it },
                        showDivider = true
                    )
                    ConfigOptionRow(
                        title    = stringResource(R.string.config_reverse_title),
                        subtitle = stringResource(R.string.config_reverse_sub),
                        icon     = "↕",
                        checked  = isReverseMode,
                        onCheckedChange = { isReverseMode = it },
                        showDivider = false
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Botó començar ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, 450)) + slideInVertically(tween(700, 450)) { 60 }
            ) {
                StartButton(
                    enabled = true,
                    onClick = {
                        if (alias.isNotBlank()) {
                            onStartGame(alias, isTimeEnabled, isBordersMode, isReverseMode)
                        } else {
                            isAliasError = true
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Fila d'opció amb Switch ──────────────────────────────────────────────────
@Composable
fun ConfigOptionRow(
    title: String,
    subtitle: String,
    icon: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCheckedChange(!checked) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icona
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(
                        1.dp,
                        if (checked) TtBluePrimary.copy(alpha = 0.6f) else TtBorder,
                        RoundedCornerShape(4.dp)
                    )
                    .background(
                        if (checked) TtBluePrimary.copy(alpha = 0.12f) else TtBgCard,
                        RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 16.sp, color = if (checked) TtBlueLight else TtTextDim)
            }

            // Textos
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (checked) TtTextPrimary else TtTextSecondary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TtTextDim,
                    letterSpacing = 0.5.sp
                )
            }

            // Switch estilitzat
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor       = TtTextPrimary,
                    checkedTrackColor       = TtBluePrimary,
                    checkedBorderColor      = TtBlueLight.copy(alpha = 0.4f),
                    uncheckedThumbColor     = TtTextDim,
                    uncheckedTrackColor     = TtBgDeep,
                    uncheckedBorderColor    = TtBorder
                )
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = TtBorder.copy(alpha = 0.5f)
            )
        }
    }
}

// ─── Botó principal de començar ───────────────────────────────────────────────
@Composable
fun StartButton(enabled: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )
    LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .border(
                width = 1.5.dp,
                color = TtGold.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(4.dp)
            )
            .background(TtBluePrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { pressed = true; onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "▶",
                color = TtGoldLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = stringResource(R.string.config_btn_start),
                fontSize = 14.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                color = TtGoldLight
            )
        }
    }
}