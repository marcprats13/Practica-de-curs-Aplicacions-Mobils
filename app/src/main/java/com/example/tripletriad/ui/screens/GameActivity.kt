package com.example.tripletriad.ui.screens

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.tripletriad.model.Card
import com.example.tripletriad.utils.GameSettings
import com.example.tripletriad.viewmodel.GameViewModel
import com.example.tripletriad.model.Player
import com.example.tripletriad.R
import com.example.tripletriad.model.GameEndReason
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay
import com.example.tripletriad.utils.IntentKeys

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerName    = intent.getStringExtra(IntentKeys.EXTRA_ALIAS) ?: "Player 1"
        val isTimeEnabled = intent.getBooleanExtra(IntentKeys.EXTRA_TIME_CONTROL, false)
        val isBordersMode = intent.getBooleanExtra(IntentKeys.EXTRA_BORDERS_MODE, false)
        val isReverseMode = intent.getBooleanExtra(IntentKeys.EXTRA_REVERSE_MODE, false)

        setContent {
            TripleTriadTheme {
                val gameViewModel: GameViewModel = viewModel()

                LaunchedEffect(Unit) {

                    gameViewModel.setGameRules(isBordersMode, isReverseMode)
                    gameViewModel.initLog(playerName, isTimeEnabled)

                    if (isTimeEnabled) gameViewModel.startTimer(GameSettings.DEFAULT_TIME_SECONDS)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    GameScreen(playerName, isTimeEnabled, gameViewModel)

                    // AlertDialog de final de partida
                    if (gameViewModel.isGameOver) {
                        val timeSpent =
                            if (isTimeEnabled) {
                                GameSettings.DEFAULT_TIME_SECONDS - gameViewModel.timeLeft
                            } else {
                                ((System.currentTimeMillis() - gameViewModel.gameStartTime) / 1000).toInt()
                            }
                        val timedOut = gameViewModel.endReason == GameEndReason.TIME_OUT
                        val outcome = when {
                            timedOut -> GameOutcome.LOSE
                            gameViewModel.playerScore > gameViewModel.opponentScore -> GameOutcome.WIN
                            gameViewModel.playerScore < gameViewModel.opponentScore -> GameOutcome.LOSE
                            else -> GameOutcome.DRAW
                        }
                        GameOverDialog(
                            outcome       = outcome,
                            timedOut      = timedOut,
                            playerScore   = gameViewModel.playerScore,
                            opponentScore = gameViewModel.opponentScore,
                            onConfirm     = {
                                val intent = Intent(this@GameActivity, ResultsActivity::class.java).apply {
                                    putExtra(IntentKeys.EXTRA_ALIAS,      playerName)
                                    putExtra(IntentKeys.EXTRA_P1_SCORE,  gameViewModel.playerScore)
                                    putExtra(IntentKeys.EXTRA_OPP_SCORE, gameViewModel.opponentScore)
                                    putExtra(IntentKeys.EXTRA_TIME_SPENT,      timeSpent)
                                    putExtra(IntentKeys.EXTRA_BORDERS_MODE, isBordersMode)
                                    putExtra(IntentKeys.EXTRA_REVERSE_MODE, isReverseMode)
                                    putExtra(IntentKeys.EXTRA_TIME_OUT, timedOut)
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
}

// Enum resultado
enum class GameOutcome { WIN, LOSE, DRAW }

// AlertDialog
@Composable
fun GameOverDialog(
    outcome: GameOutcome,
    timedOut: Boolean,
    playerScore: Int,
    opponentScore: Int,
    onConfirm: () -> Unit
) {
    val accentColor = when (outcome) {
        GameOutcome.WIN  -> TtGoldLight
        GameOutcome.LOSE -> TtOpponentRed
        GameOutcome.DRAW -> TtBlueLight
    }
    val titleRes = when {
        timedOut -> R.string.dialog_timeout_title
        outcome == GameOutcome.WIN  -> R.string.dialog_win_title
        outcome == GameOutcome.LOSE -> R.string.dialog_lose_title
        else -> R.string.dialog_draw_title
    }
    val icon = when (outcome) {
        GameOutcome.WIN  -> R.string.dialog_win_icon
        GameOutcome.LOSE -> R.string.dialog_lose_icon
        GameOutcome.DRAW -> R.string.dialog_draw_icon
    }

    // Animacion de entrada
    val infiniteTransition = rememberInfiniteTransition(label = "dialog_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )

    // Overlay oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        // Targeta del diàleg
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .border(
                    width = 1.5.dp,
                    color = accentColor.copy(alpha = glowAlpha),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    Brush.verticalGradient(listOf(TtBgSurface, TtBgDeep)),
                    RoundedCornerShape(8.dp)
                )
                .padding(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icono grande
                Text(
                    text = stringResource(icon),
                    fontSize = 40.sp,
                    color = accentColor,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(color = accentColor.copy(alpha = glowAlpha), blurRadius = 20f)
                    )
                )

                // Título
                Text(
                    text = stringResource(titleRes),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = accentColor,
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(color = accentColor.copy(alpha = 0.5f), blurRadius = 16f)
                    )
                )

                HorizontalDivider(color = TtBorder, thickness = 1.dp)

                // Marcador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScoreChip(label = "TU", score = playerScore,   color = TtPlayerBlue)
                    Text("—", color = TtTextDim, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    ScoreChip(label = "RIVAL", score = opponentScore, color = TtOpponentRed)
                }

                HorizontalDivider(color = TtBorder, thickness = 1.dp)

                // Botón de confirmar
                var pressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (pressed) 0.96f else 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "btn_scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .border(1.5.dp, accentColor, RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { pressed = true; onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.dialog_btn_results),
                        modifier = Modifier.padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }

                LaunchedEffect(pressed) {
                    if (pressed) { delay(150); pressed = false }
                }
            }
        }
    }
}

@Composable
fun ScoreChip(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$score",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = color,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(color = color.copy(alpha = 0.4f), blurRadius = 12f)
            )
        )
        Text(text = label, fontSize = 9.sp, letterSpacing = 2.sp, color = TtTextSecondary)
    }
}

// Game Screen: decide entre mono-panel (móvil) y bi-panel (tablet)
@Composable
fun GameScreen(
    playerName: String,
    isTimeEnabled: Boolean,
    viewModel: GameViewModel
) {
    // Miramos si Window Size Class: expanded para tablet
    val widthSizeClass = currentWindowAdaptiveInfo()
        .windowSizeClass.windowWidthSizeClass
    val isTablet = widthSizeClass == WindowWidthSizeClass.EXPANDED

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(TtBgDeep)
        ) {
            // Panel principal: el juego
            Box(modifier = Modifier.weight(2.5f).fillMaxHeight()) {
                GameScreenMono(playerName, isTimeEnabled, viewModel)
            }
            // Panel secundario: el log de jugadas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                GameLogPane(viewModel)
            }
        }
    } else {
        // MÓVIL -> mono-panel: solo el juego, como hasta ahora
        GameScreenMono(playerName, isTimeEnabled, viewModel)
    }
}

// Game Screen (Monopanel)
@Composable
fun GameScreenMono(
    playerName: String,
    isTimeEnabled: Boolean,
    viewModel: GameViewModel
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(TtBgDeep)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puntuaciones derecha
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameHeader(playerName, isTimeEnabled, viewModel)
                ScoreBar(viewModel)
                Spacer(Modifier.height(16.dp))
                HandRow(viewModel.opponentHand, TtOpponentRed) { }
                Spacer(Modifier.height(16.dp))
                HandRow(viewModel.playerHand, TtPlayerBlue) { card -> viewModel.selectCard(card) }
            }
            // Tablero izquierda
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .aspectRatio(1f)
                    .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
                    .background(TtBgSurface, RoundedCornerShape(6.dp))
            ) {
                GameBoard(viewModel)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TtBgDeep)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            GameHeader(playerName, isTimeEnabled, viewModel)
            Spacer(Modifier.height(16.dp))
            ScoreBar(viewModel)
            Spacer(Modifier.height(24.dp))
            HandRow(viewModel.opponentHand, TtOpponentRed) { }
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
                    .background(TtBgSurface, RoundedCornerShape(6.dp))
            ) {
                GameBoard(viewModel)
            }
            Spacer(Modifier.height(24.dp))
            HandRow(viewModel.playerHand, TtPlayerBlue) { card -> viewModel.selectCard(card) }
        }
    }
}

// Panel del log de jugadas
@Composable
fun GameLogPane(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
            .background(TtBgSurface, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.game_log),
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.SemiBold,
            color = TtGold
        )
        if (viewModel.logHeader.isNotEmpty()) {
            Text(
                text = viewModel.logHeader.joinToString("  -  "),
                fontSize = 10.sp,
                color = TtTextSecondary
            )
        }

        Spacer(Modifier.height(6.dp))
        HorizontalDivider(color = TtBorder)
        Spacer(Modifier.height(6.dp))

        if (viewModel.gameLog.isEmpty()) {
            Text(
                text = stringResource(R.string.game_no_play),
                fontSize = 10.sp,
                color = TtTextSecondary
            )
        } else {
            // Lista de jugadas, lo hacemos scrolleable si se pasa de mas
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                viewModel.gameLog.forEachIndexed { index, entrada ->
                    Text(
                        text = "${index + 1}. $entrada",
                        fontSize = 10.sp,
                        color = TtTextPrimary
                    )
                }
            }
        }
    }
}
@Composable
fun GameBoard(viewModel: GameViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize().padding(4.dp)
    ) {
        items(9) { index ->
            BoardCell(card = viewModel.board[index], onClick = { viewModel.playCard(index) })
        }
    }
}

@Composable
fun GameHeader(playerName: String, isTimeEnabled: Boolean, viewModel: GameViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = playerName.uppercase(),
            fontSize = 13.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.SemiBold,
            color = TtTextSecondary
        )
        if (isTimeEnabled) {
            Spacer(Modifier.height(4.dp))
            val timeColor = if (viewModel.timeLeft <= 10) TtOpponentRed else TtTextSecondary
            Text(
                text = "${viewModel.timeLeft}s",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = timeColor
            )
        }
    }
}

@Composable
fun ScoreBar(viewModel: GameViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
            .background(TtBgSurface, RoundedCornerShape(6.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${viewModel.playerScore}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TtPlayerBlue)
            Text("TU", fontSize = 9.sp, letterSpacing = 2.sp, color = TtTextSecondary)
        }
        Box(
            modifier = Modifier
                .border(1.dp, TtBorder, RoundedCornerShape(4.dp))
                .background(TtBgCard, RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (viewModel.isPlayer1Turn)
                    stringResource(R.string.game_turn_yours)
                else
                    stringResource(R.string.game_turn_opponent),
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = if (viewModel.isPlayer1Turn) TtGoldLight else TtTextDim
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${viewModel.opponentScore}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TtOpponentRed)
            Text("RIVAL", fontSize = 9.sp, letterSpacing = 2.sp, color = TtTextSecondary)
        }
    }
}

@Composable
fun HandRow(hand: List<Card>, color: Color, onCardClick: (Card) -> Unit) {
    val viewModel: com.example.tripletriad.viewmodel.GameViewModel = viewModel()
    val selectedCard = viewModel.selectedCard

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),

        horizontalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        hand.forEach { card ->

            val isSelected = card == selectedCard

            Box(
                modifier = Modifier

                    .offset(y = if (isSelected) (-12).dp else 0.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCardClick(card) }

                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) TtGold else TtBorder.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .requiredWidth(46.dp)
            ) {
                CardView(card, color = color)
            }
        }
    }
}

// Celdas del tablero
@Composable
fun BoardCell(card: Card?, onClick: () -> Unit) {
    val bgColor = if (card == null) TtBgCard else when (card.owner) {
        Player.PLAYER_1 -> TtPlayerBlue.copy(alpha = 0.15f)
        Player.OPPONENT -> TtOpponentRed.copy(alpha = 0.15f)
        else            -> TtBgCard
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .border(1.dp, TtBorder, RoundedCornerShape(4.dp))
            .background(bgColor, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (card != null) {
            val cardColor = when (card.owner) {
                Player.PLAYER_1 -> TtPlayerBlue
                Player.OPPONENT -> TtOpponentRed
                else            -> TtTextDim
            }
            CardView(card, cardColor)
        }
    }
}

//Vista de cartas
@Composable
fun CardView(card: Card, color: Color = TtPlayerBlue) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(50.dp, 64.dp)
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(listOf(color.copy(alpha = 0.2f), TtBgDeep)),
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Top
        Text(
            text = "${card.top}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        )
        // Bottom
        Text(
            text = "${card.bottom}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
        // Left
        Text(
            text = "${card.left}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
        )
        // Right
        Text(
            text = "${card.right}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
        )
        // Centro vacio
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.Center)
                .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
        )
    }
}