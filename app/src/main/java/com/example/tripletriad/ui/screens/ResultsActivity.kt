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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.utils.EmailConfig
import com.example.tripletriad.utils.GameSettings
import com.example.tripletriad.R
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.tripletriad.viewmodel.ResultsViewModel
import com.example.tripletriad.utils.IntentKeys
import com.example.tripletriad.utils.AnimationConfig

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alias = intent.getStringExtra(IntentKeys.EXTRA_ALIAS) ?: "Invitat"
        val time  = intent.getIntExtra(IntentKeys.EXTRA_TIME_SPENT, 0)
        val p1    = intent.getIntExtra(IntentKeys.EXTRA_P1_SCORE, 0)
        val opp   = intent.getIntExtra(IntentKeys.EXTRA_OPP_SCORE, 0)
        val borders = intent.getBooleanExtra(IntentKeys.EXTRA_BORDERS_MODE, false)
        val reverse = intent.getBooleanExtra(IntentKeys.EXTRA_REVERSE_MODE, false)

        val size = GameSettings.DEFAULT_GRID_SIZE

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val now = LocalDateTime.now().format(formatter)

        val logResumen = """
            RESUMEN DE LA PARTIDA
            Alias: $alias
            Mida Parrilla: ${size}x${size}
            Tiempo empleado: $time segundos
            Resultado: ${if (p1 > opp) "Victoria" else "Derrorta"} ($p1 - $opp)
            Finalizado el: $now
            Modo Fronteras: ${if (borders) "Activado" else "Desactivado"}
            Modo Inverso: ${if (reverse) "Activado" else "Desactivado"}
        """.trimIndent()

        setContent {
            TripleTriadTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = TtBgDeep) {

                    val viewModel: ResultsViewModel = viewModel()

                    LaunchedEffect(Unit) {
                        viewModel.initData(subject = now, log = logResumen)
                    }

                    ResultsScreen(
                        playerName  = alias,
                        gridSize    = size,
                        timeSpent   = time,
                        p1Score     = p1,
                        oppScore    = opp,
                        dateTime    = now,
                        viewModel   = viewModel,
                        onSend = { email, subject, body ->
                            val intentEmail = Intent(Intent.ACTION_SEND).apply {
                                type = EmailConfig.MIME_TYPE
                                putExtra(Intent.EXTRA_EMAIL,   arrayOf(email))
                                putExtra(Intent.EXTRA_SUBJECT, subject)
                                putExtra(Intent.EXTRA_TEXT,    body)
                            }
                            startActivity(Intent.createChooser(intentEmail,
                                getString(R.string.results_chooser)))
                        },
                        onPlayAgain = {
                            startActivity(Intent(this, ConfigurationActivity::class.java))
                            finish()
                        },
                        onExit = { finishAffinity() }
                    )
                }
            }
        }
    }
}

// Results Screen
@Composable
fun ResultsScreen(
    playerName: String,
    gridSize: Int,
    timeSpent: Int,
    p1Score: Int,
    oppScore: Int,
    dateTime: String,
    viewModel: ResultsViewModel,
    onSend: (String, String, String) -> Unit,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val outcome = when {
        p1Score > oppScore -> GameOutcome.WIN
        p1Score < oppScore -> GameOutcome.LOSE
        else               -> GameOutcome.DRAW
    }
    val accentColor = when (outcome) {
        GameOutcome.WIN  -> TtGoldLight
        GameOutcome.LOSE -> TtOpponentRed
        GameOutcome.DRAW -> TtBlueLight
    }
    val outcomeIcon = when (outcome) {
        GameOutcome.WIN  -> "★"
        GameOutcome.LOSE -> "✕"
        GameOutcome.DRAW -> "◆"
    }

    // Glow animado
    val infiniteTransition = rememberInfiniteTransition(label = "res_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        MenuBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Titulo
            AnimatedVisibility(visible, enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL)) + slideInVertically(tween(
                AnimationConfig.DURATION_NORMAL)) { -60 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalDividerWithDiamonds()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.results_title),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 10.sp,
                        color = TtTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.results_subtitle),
                        fontSize = 10.sp,
                        letterSpacing = 3.sp,
                        color = TtTextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDividerWithDiamonds()
                }
            }

            // ── Bloc resultat ─────────────────────────────────────────────
            AnimatedVisibility(visible, enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL,
                AnimationConfig.DELAY_SHORT)) + scaleIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_SHORT))) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            accentColor.copy(alpha = glowAlpha),
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            Brush.verticalGradient(listOf(TtBgSurface, TtBgDeep)),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Icono y resultado
                        Text(
                            text = outcomeIcon,
                            fontSize = 36.sp,
                            color = accentColor,
                            style = LocalTextStyle.current.copy(
                                shadow = Shadow(color = accentColor.copy(alpha = glowAlpha), blurRadius = 20f)
                            )
                        )
                        Text(
                            text = stringResource(
                                when (outcome) {
                                    GameOutcome.WIN  -> R.string.results_win
                                    GameOutcome.LOSE -> R.string.results_lose
                                    GameOutcome.DRAW -> R.string.results_draw
                                }
                            ),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 5.sp,
                            color = accentColor,
                            style = LocalTextStyle.current.copy(
                                shadow = Shadow(color = accentColor.copy(alpha = 0.4f), blurRadius = 12f)
                            )
                        )

                        HorizontalDivider(color = TtBorder)

                        // Marcador
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreChip(
                                label = playerName.uppercase(),
                                score = p1Score,
                                color = TtPlayerBlue
                            )
                            Text("—", color = TtTextDim, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            ScoreChip(
                                label = stringResource(R.string.results_machine_label),
                                score = oppScore,
                                color = TtOpponentRed
                            )
                        }
                    }
                }
            }

            // Estadísticas
            AnimatedVisibility(visible, enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL,
                AnimationConfig.DELAY_MEDIUM)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL,
                AnimationConfig.DELAY_MEDIUM)) { 40 }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TtBorder, RoundedCornerShape(8.dp))
                        .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        stringResource(R.string.results_stats_title),
                        fontSize = 10.sp, letterSpacing = 3.sp,
                        color = TtGold, fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(color = TtBorder.copy(alpha = 0.5f))
                    StatRow(label = stringResource(R.string.results_stat_size),
                        value = stringResource(R.string.results_stat_grid, gridSize))
                    StatRow(label = stringResource(R.string.results_stat_time),
                        value = stringResource(R.string.results_stat_seconds, timeSpent))
                    StatRow(label = stringResource(R.string.results_stat_date), value = dateTime)
                }
            }

            //Log Editable
            AnimatedVisibility(visible, enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL,
                AnimationConfig.DELAY_LONG))) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TtBorder, RoundedCornerShape(8.dp))
                        .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.results_log_title),
                        fontSize = 10.sp, letterSpacing = 3.sp,
                        color = TtGold, fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(color = TtBorder.copy(alpha = 0.5f))

                    // Email
                    TtOutlinedField(
                        value = viewModel.emailRecipient,
                        onValueChange = { viewModel.emailRecipient = it },
                        label = stringResource(R.string.results_email_label),
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                    // Asunto
                    TtOutlinedField(
                        value = viewModel.emailSubject,
                        onValueChange = { viewModel.emailSubject = it },
                        label = stringResource(R.string.results_subject_label)
                    )
                    // Log
                    TtOutlinedField(
                        value = viewModel.logBody,
                        onValueChange = { viewModel.logBody = it },
                        label = "Log",
                        modifier = Modifier.height(150.dp),
                        singleLine = false
                    )

                    // Botón de enviar
                    TtButton(
                        label = stringResource(R.string.results_btn_send),
                        color = TtGold,
                        onClick = { onSend(viewModel.emailRecipient, viewModel.emailSubject, viewModel.logBody) }
                    )
                }
            }

            // Botones finales de nueva partida y salir
            AnimatedVisibility(visible, enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL,
                AnimationConfig.DELAY_LONG_2)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL,
                AnimationConfig.DELAY_LONG_2)) { 40 }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TtButton(
                        label = stringResource(R.string.results_btn_play_again),
                        color = TtPlayerBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onPlayAgain
                    )
                    TtButton(
                        label = stringResource(R.string.results_btn_exit),
                        color = TtOpponentRed,
                        modifier = Modifier.weight(1f),
                        onClick = onExit
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// Components reutilitzables
@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TtTextSecondary)
        Text(value, fontSize = 12.sp, color = TtTextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TtButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btn_scale"
    )
    LaunchedEffect(pressed) { if (pressed) {
        delay(150); pressed = false } }

    Box(
        modifier = modifier
            .scale(scale)
            .border(1.5.dp, color, RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                pressed = true; onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TtOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp, color = TtTextSecondary) },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = TtBluePrimary,
            unfocusedBorderColor = TtBorder,
            focusedTextColor     = TtTextPrimary,
            unfocusedTextColor   = TtTextPrimary,
            cursorColor          = TtBlueLight,
            focusedLabelColor    = TtBlueLight,
            unfocusedLabelColor  = TtTextDim,
            focusedContainerColor   = TtBgCard,
            unfocusedContainerColor = TtBgCard
        )
    )
}