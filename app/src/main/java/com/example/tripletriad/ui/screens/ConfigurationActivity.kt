package com.example.tripletriad.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.tripletriad.R
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay
import com.example.tripletriad.utils.IntentKeys
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.viewmodel.ConfigurationViewModel
import com.example.tripletriad.utils.AnimationConfig

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    val viewModel: ConfigurationViewModel = viewModel()

                    ConfiguracionScreen(
                        viewModel = viewModel,
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
fun ConfiguracionScreen(
    viewModel: ConfigurationViewModel,
    onStartGame: (String, Boolean, Boolean, Boolean) -> Unit) {

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(AnimationConfig.INITIAL_START_DELAY); visible = true }

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

            // Cabecera
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL, easing = EaseOutCubic)) { -60 }
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

            // Alias
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_SHORT)) + slideInHorizontally(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_SHORT)) { -40 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (viewModel.isAliasError) 1.5.dp else 1.dp,
                            color = if (viewModel.isAliasError) TtOpponentRed else TtBorder,
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
                        value = viewModel.alias,
                        onValueChange = {
                            viewModel.alias = it
                            if (it.isNotBlank()) viewModel.isAliasError = false
                        },
                        placeholder = { Text("ex: Jugador", color = TtTextDim, fontSize = 14.sp) },
                        singleLine = true,
                        isError = viewModel.isAliasError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor       = TtBluePrimary,
                            unfocusedBorderColor     = TtBorder,
                            errorBorderColor         = TtOpponentRed,
                            focusedTextColor         = TtTextPrimary,
                            unfocusedTextColor       = TtTextPrimary,
                            cursorColor              = TtBlueLight,
                            focusedContainerColor    = TtBgCard,
                            unfocusedContainerColor  = TtBgCard,
                            errorContainerColor      = TtOpponentRed.copy(alpha = 0.05f)
                        )
                    )
                    AnimatedVisibility(visible = viewModel.isAliasError) {
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

            // Opciones de juego
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_MEDIUM)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_MEDIUM)) { 40 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
                        .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(4.dp)
                ) {
                    ConfigOptionRow(
                        title       = stringResource(R.string.config_time_title),
                        subtitle    = stringResource(R.string.config_time_sub),
                        icon        = "⏱",
                        checked     = viewModel.isTimeEnabled,
                        onCheckedChange = { viewModel.isTimeEnabled = it },
                        showDivider = true
                    )
                    ConfigOptionRow(
                        title       = stringResource(R.string.config_borders_title),
                        subtitle    = stringResource(R.string.config_borders_sub),
                        icon        = "⊕",
                        checked     = viewModel.isBordersMode,
                        onCheckedChange = { viewModel.isBordersMode = it },
                        showDivider = true
                    )
                    ConfigOptionRow(
                        title       = stringResource(R.string.config_reverse_title),
                        subtitle    = stringResource(R.string.config_reverse_sub),
                        icon        = "↕",
                        checked     = viewModel.isReverseMode,
                        onCheckedChange = { viewModel.isReverseMode = it },
                        showDivider = false
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botón de comenzar partida
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_LONG)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_LONG)) { 60 }
            ) {
                StartButton(
                    onClick = {
                        if (viewModel.alias.isNotBlank()) {
                            onStartGame(viewModel.alias, viewModel.isTimeEnabled, viewModel.isBordersMode, viewModel.isReverseMode)
                        } else {
                            viewModel.isAliasError = true
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// Para las filas de opciones de juego
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
                Text(
                    text = icon,
                    fontSize = 16.sp,
                    color = if (checked) TtBlueLight else TtTextDim
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
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

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor    = TtTextPrimary,
                    checkedTrackColor    = TtBluePrimary,
                    checkedBorderColor   = TtBlueLight.copy(alpha = 0.4f),
                    uncheckedThumbColor  = TtTextDim,
                    uncheckedTrackColor  = TtBgDeep,
                    uncheckedBorderColor = TtBorder
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

// Botón principal
@Composable
fun StartButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(AnimationConfig.DURATION_LONG, easing = EaseInOutSine), RepeatMode.Reverse
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
            .border(1.5.dp, TtGold.copy(alpha = glowAlpha), RoundedCornerShape(4.dp))
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
            Text("▶", color = TtGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Black)
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