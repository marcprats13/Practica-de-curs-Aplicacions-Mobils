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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerName    = intent.getStringExtra(IntentKeys.EXTRA_ALIAS) ?: "Player 1"
        val gridSize      = intent.getIntExtra(IntentKeys.EXTRA_SIZE, 3)
        val isTimeEnabled = intent.getBooleanExtra(IntentKeys.EXTRA_TIME_CONTROL, false)
        val isBordersMode = intent.getBooleanExtra(IntentKeys.EXTRA_BORDERS_MODE, false)
        val isReverseMode = intent.getBooleanExtra(IntentKeys.EXTRA_REVERSE_MODE, false)

        setContent {
            TripleTriadTheme {
                val gameViewModel: GameViewModel = viewModel()

                LaunchedEffect(Unit) {

                    gameViewModel.setGameRules(isBordersMode, isReverseMode)
                    if (isTimeEnabled) gameViewModel.startTimer(GameSettings.DEFAULT_TIME_SECONDS)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    GameScreen(playerName, gridSize, isTimeEnabled, gameViewModel)

                    // ── AlertDialog de final de partida ──────────────────────
                    if (gameViewModel.isGameOver) {
                        val timeSpent = if (isTimeEnabled) 50 - gameViewModel.timeLeft else 0
                        val outcome = when {
                            gameViewModel.playerScore > gameViewModel.opponentScore -> GameOutcome.WIN
                            gameViewModel.playerScore < gameViewModel.opponentScore -> GameOutcome.LOSE
                            else -> GameOutcome.DRAW
                        }
                        GameOverDialog(
                            outcome       = outcome,
                            playerScore   = gameViewModel.playerScore,
                            opponentScore = gameViewModel.opponentScore,
                            onConfirm     = {
                                val intent = Intent(this@GameActivity, ResultsActivity::class.java).apply {
                                    putExtra(IntentKeys.EXTRA_ALIAS,      playerName)
                                    putExtra(IntentKeys.EXTRA_SIZE,      3)
                                    putExtra(IntentKeys.EXTRA_P1_SCORE,  gameViewModel.playerScore)
                                    putExtra(IntentKeys.EXTRA_OPP_SCORE, gameViewModel.opponentScore)
                                    putExtra(IntentKeys.EXTRA_TIME_SPENT,      timeSpent)
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

// ─── Enum resultado ────────────────────────────────────────────────────────────
enum class GameOutcome { WIN, LOSE, DRAW }

// ─── AlertDialog ───────────────────────────────────────────────────
@Composable
fun GameOverDialog(
    outcome: GameOutcome,
    playerScore: Int,
    opponentScore: Int,
    onConfirm: () -> Unit
) {
    val accentColor = when (outcome) {
        GameOutcome.WIN  -> TtGoldLight
        GameOutcome.LOSE -> TtOpponentRed
        GameOutcome.DRAW -> TtBlueLight
    }
    val titleRes = when (outcome) {
        GameOutcome.WIN  -> R.string.dialog_win_title
        GameOutcome.LOSE -> R.string.dialog_lose_title
        GameOutcome.DRAW -> R.string.dialog_draw_title
    }
    val subRes = when (outcome) {
        GameOutcome.WIN  -> R.string.dialog_win_sub
        GameOutcome.LOSE -> R.string.dialog_lose_sub
        GameOutcome.DRAW -> R.string.dialog_draw_sub
    }
    val icon = when (outcome) {
        GameOutcome.WIN  -> "★"
        GameOutcome.LOSE -> "✕"
        GameOutcome.DRAW -> "◆"
    }

    // Animació d'entrada
    val infiniteTransition = rememberInfiniteTransition(label = "dialog_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )

    // Overlay fosc
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
                // Icona gran
                Text(
                    text = icon,
                    fontSize = 40.sp,
                    color = accentColor,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(color = accentColor.copy(alpha = glowAlpha), blurRadius = 20f)
                    )
                )

                // Títol
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

                // Subtítol
                Text(
                    text = stringResource(subRes),
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = TtTextSecondary,
                    textAlign = TextAlign.Center
                )

                // Divider
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

                // Botó confirmar
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

// ─── Game Screen ──────────────────────────────────────────────────────────────
@Composable
fun GameScreen(
    playerName: String,
    gridSize: Int,
    isTimeEnabled: Boolean,
    viewModel: GameViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TtBgDeep)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // ── Capçalera ────────────────────────────────────────────────────
        Text(
            text = playerName.uppercase(),
            fontSize = 13.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.SemiBold,
            color = TtTextSecondary
        )

        // Temporitzador
        if (isTimeEnabled) {
            Spacer(Modifier.height(4.dp))
            val timeColor = if (viewModel.timeLeft <= 10) TtOpponentRed else TtTextSecondary
            Text(
                text = "${viewModel.timeLeft}s",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = timeColor,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(color = timeColor.copy(alpha = 0.5f), blurRadius = 10f)
                )
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Marcador ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
                .background(TtBgSurface, RoundedCornerShape(6.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Jugador
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${viewModel.playerScore}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TtPlayerBlue)
                Text(text = "TU", fontSize = 9.sp, letterSpacing = 2.sp, color = TtTextSecondary)
            }
            // Torn
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
            // Rival
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${viewModel.opponentScore}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TtOpponentRed)
                Text(text = "RIVAL", fontSize = 9.sp, letterSpacing = 2.sp, color = TtTextSecondary)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Tauler 3x3 ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
                .background(TtBgSurface, RoundedCornerShape(6.dp))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                items(9) { index ->
                    BoardCell(card = viewModel.board[index], onClick = { viewModel.playCard(index) })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Mà del jugador ───────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = TtBorder)
            Text(stringResource(R.string.game_your_hand), fontSize = 9.sp, letterSpacing = 3.sp, color = TtTextDim)
            HorizontalDivider(modifier = Modifier.weight(1f), color = TtBorder)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            viewModel.playerHand.forEach { card ->
                val isSelected = card == viewModel.selectedCard
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.selectCard(card) }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) TtGoldLight else TtBorder,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(
                            if (isSelected) TtGold.copy(alpha = 0.1f) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .scale(if (isSelected) 1.06f else 1f)
                ) {
                    CardView(card)
                }
            }
        }
    }
}

// ─── Cel·la del tauler ────────────────────────────────────────────────────────
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

// ─── Vista de carta ───────────────────────────────────────────────────────────
@Composable
fun CardView(card: Card, color: Color = TtPlayerBlue) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(56.dp, 72.dp)
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
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
        )
        // Bottom
        Text(
            text = "${card.bottom}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
        )
        // Left
        Text(
            text = "${card.left}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
        )
        // Right
        Text(
            text = "${card.right}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = color,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
        )
        // Centre buit
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.Center)
                .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
        )
    }
}