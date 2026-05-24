package com.example.tripletriad.ui.screens

import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.GameApplication
import com.example.tripletriad.utils.EmailConfig
import com.example.tripletriad.utils.GameSettings
import com.example.tripletriad.R
import com.example.tripletriad.data.PartidaEntity
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.tripletriad.viewmodel.ResultsViewModel
import com.example.tripletriad.utils.IntentKeys
import com.example.tripletriad.utils.AnimationConfig
import com.example.tripletriad.viewmodel.MainViewModel
import com.example.tripletriad.viewmodel.PartidaViewModel
import com.example.tripletriad.viewmodel.PartidaViewModelFactory

class ResultsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alias = intent.getStringExtra(IntentKeys.EXTRA_ALIAS) ?: "Invitat"
        val time  = intent.getIntExtra(IntentKeys.EXTRA_TIME_SPENT, 0)
        val p1    = intent.getIntExtra(IntentKeys.EXTRA_P1_SCORE, 0)
        val opp   = intent.getIntExtra(IntentKeys.EXTRA_OPP_SCORE, 0)
        val borders = intent.getBooleanExtra(IntentKeys.EXTRA_BORDERS_MODE, false)
        val reverse = intent.getBooleanExtra(IntentKeys.EXTRA_REVERSE_MODE, false)
        val timedOut = intent.getBooleanExtra(IntentKeys.EXTRA_TIME_OUT, false)

        val size = GameSettings.DEFAULT_GRID_SIZE

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val now = LocalDateTime.now().format(formatter)

        val resultadoTexto = when {
            timedOut -> "Derrota (tiempo agotado)"
            p1 > opp -> "Victoria"
            p1 < opp -> "Derrota"
            else     -> "Empate"
        }

        val logResumen = """
            RESUMEN DE LA PARTIDA
            Alias: $alias
            Mida Parrilla: ${size}x${size}
            Tiempo empleado: $time segundos
            Resultado: $resultadoTexto ($p1 - $opp)
            Finalizado el: $now
            Modo Fronteras: ${if (borders) "Activado" else "Desactivado"}
            Modo Inverso: ${if (reverse) "Activado" else "Desactivado"}
        """.trimIndent()

        setContent {
            TripleTriadTheme {
                val viewModel: ResultsViewModel = viewModel()
                val partidaViewModel: PartidaViewModel = viewModel(
                    factory = PartidaViewModelFactory(
                        (application as GameApplication).repository
                    )
                )
                val mainViewModel: MainViewModel = viewModel()

                LaunchedEffect(Unit) {
                    viewModel.initData(subject = now, log = logResumen)
                }

                LaunchedEffect(Unit) {
                    partidaViewModel.insert(
                        PartidaEntity(
                            alias = alias,
                            fechaHora = now,
                            tamParrilla = size,
                            modoFronteras = borders,
                            modoInverso = reverse,
                            tiempoEmpleado = time,
                            puntosJugador = p1,
                            puntosEnemigo = opp,
                            resultado = resultadoTexto
                        )
                    )
                }

                Scaffold(
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    startActivity(
                                        Intent(this@ResultsActivity, ConfigurationActivity::class.java)
                                    )
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text("⚙", fontSize = 22.sp, color = TtGoldLight)
                            }
                        }
                    },
                    containerColor = TtBgDeep
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.Transparent
                    ) {
                        ResultsScreen(
                            playerName  = alias,
                            gridSize    = size,
                            timeSpent   = time,
                            p1Score     = p1,
                            oppScore    = opp,
                            dateTime    = now,
                            timedOut    = timedOut,
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
                                if (mainViewModel.hasPreferences()) {
                                    val intent = Intent(this@ResultsActivity, GameActivity::class.java).apply {
                                        putExtra(IntentKeys.EXTRA_ALIAS,        mainViewModel.alias)
                                        putExtra(IntentKeys.EXTRA_TIME_CONTROL, mainViewModel.isTimeEnabled)
                                        putExtra(IntentKeys.EXTRA_BORDERS_MODE, mainViewModel.isBordersMode)
                                        putExtra(IntentKeys.EXTRA_REVERSE_MODE, mainViewModel.isReverseMode)
                                    }
                                    startActivity(intent)
                                } else {
                                    startActivity(Intent(this@ResultsActivity, ConfigurationActivity::class.java))
                                }
                                finish()
                            },
                            onExit = { finishAffinity() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(
    playerName: String,
    gridSize: Int,
    timeSpent: Int,
    p1Score: Int,
    oppScore: Int,
    dateTime: String,
    timedOut: Boolean,
    viewModel: ResultsViewModel,
    onSend: (String, String, String) -> Unit,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val outcome = when {
        timedOut -> GameOutcome.LOSE
        p1Score > oppScore -> GameOutcome.WIN
        p1Score < oppScore -> GameOutcome.LOSE
        else -> GameOutcome.DRAW
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

    val infiniteTransition = rememberInfiniteTransition(label = "res_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TtBgDeep),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape) {
            // Landscape: centrado horizontal y verticalmente
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .background(TtBgSurface.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna izquierda - Resultado y Estadísticas
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ResultBlock(outcome, outcomeIcon, accentColor, glowAlpha, playerName, p1Score, oppScore)
                    Spacer(Modifier.height(16.dp))
                    StatsBlock(gridSize, timeSpent, dateTime)
                }

                // Columna derecha - Log y Botones
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogBlock(viewModel, focusRequester, onSend)
                    Spacer(Modifier.height(16.dp))
                    FinalButtons(onPlayAgain, onExit)
                }
            }
        } else {
            // Portrait: centrado verticalmente con scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .background(TtBgSurface.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Cabecera decorativa
                HorizontalDividerWithDiamonds()
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.results_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = TtTextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.results_subtitle),
                    fontSize = 10.sp,
                    letterSpacing = 3.sp,
                    color = TtTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDividerWithDiamonds()

                Spacer(Modifier.height(20.dp))

                ResultBlock(outcome, outcomeIcon, accentColor, glowAlpha, playerName, p1Score, oppScore)

                Spacer(Modifier.height(16.dp))

                StatsBlock(gridSize, timeSpent, dateTime)

                Spacer(Modifier.height(16.dp))

                LogBlock(viewModel, focusRequester, onSend)

                Spacer(Modifier.height(16.dp))

                FinalButtons(onPlayAgain, onExit)

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ResultBlock(
    outcome: GameOutcome,
    outcomeIcon: String,
    accentColor: Color,
    glowAlpha: Float,
    playerName: String,
    p1Score: Int,
    oppScore: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, accentColor.copy(alpha = glowAlpha), RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(TtBgSurface, TtBgDeep)), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = outcomeIcon,
                fontSize = 28.sp,
                color = accentColor,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(color = accentColor.copy(alpha = glowAlpha), blurRadius = 20f)
                )
            )
            Text(
                text = stringResource(
                    when {
                        outcome == GameOutcome.WIN  -> R.string.results_win
                        outcome == GameOutcome.LOSE -> R.string.results_lose
                        else                        -> R.string.results_draw
                    }
                ),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = accentColor,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(color = accentColor.copy(alpha = 0.4f), blurRadius = 12f)
                )
            )
            HorizontalDivider(color = TtBorder)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreChip(label = playerName.uppercase(), score = p1Score, color = TtPlayerBlue)
                Text("—", color = TtTextDim, fontSize = 24.sp, fontWeight = FontWeight.Black)
                ScoreChip(label = stringResource(R.string.results_machine_label), score = oppScore, color = TtOpponentRed)
            }
        }
    }
}

@Composable
fun StatsBlock(gridSize: Int, timeSpent: Int, dateTime: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(8.dp))
            .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(R.string.results_stats_title),
            fontSize = 11.sp, letterSpacing = 3.sp,
            color = TtGold, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        StatRow(label = stringResource(R.string.results_stat_size),
            value = stringResource(R.string.results_stat_grid, gridSize))
        StatRow(label = stringResource(R.string.results_stat_time),
            value = stringResource(R.string.results_stat_seconds, timeSpent))
        StatRow(label = stringResource(R.string.results_stat_date), value = dateTime)
    }
}

@Composable
fun LogBlock(
    viewModel: ResultsViewModel,
    focusRequester: FocusRequester,
    onSend: (String, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(8.dp))
            .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ENVIAR POR CORREO",
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = TtGold,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        TtOutlinedField(
            value = viewModel.emailRecipient,
            onValueChange = { viewModel.updateRecipient(it) },
            label = stringResource(R.string.results_email_label),
            modifier = Modifier.focusRequester(focusRequester)
        )
        TtOutlinedField(
            value = viewModel.emailSubject,
            onValueChange = { viewModel.updateSubject(it) },
            label = stringResource(R.string.results_subject_label)
        )
        TtOutlinedField(
            value = viewModel.logBody,
            onValueChange = { viewModel.updateLogBody(it) },
            label = "Log",
            modifier = Modifier.height(80.dp),
            singleLine = false
        )
        TtButton(
            label = stringResource(R.string.results_btn_send),
            color = TtGold,
            onClick = { onSend(viewModel.emailRecipient, viewModel.emailSubject, viewModel.logBody) }
        )
    }
}

@Composable
fun FinalButtons(onPlayAgain: () -> Unit, onExit: () -> Unit) {
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
    LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }

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
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
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