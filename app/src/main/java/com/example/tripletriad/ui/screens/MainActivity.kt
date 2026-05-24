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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.R
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay
import com.example.tripletriad.utils.AnimationConfig
import com.example.tripletriad.utils.IntentKeys
import com.example.tripletriad.viewmodel.MainViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                val mainViewModel: MainViewModel = viewModel()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
                            actions = {
                                IconButton(
                                    onClick = { startActivity(Intent(this@MainActivity, ConfigurationActivity::class.java)) }
                                ) {
                                    Text("⚙", fontSize = 24.sp, color = TtGoldLight)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                        )
                    },
                    containerColor = TtBgDeep
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.Transparent
                    ) {
                        MainMenuScreen(
                            onStartGame = {
                                if (mainViewModel.hasPreferences()) {
                                    val intent = Intent(this@MainActivity, GameActivity::class.java).apply {
                                        putExtra(IntentKeys.EXTRA_ALIAS,        mainViewModel.alias)
                                        putExtra(IntentKeys.EXTRA_TIME_CONTROL, mainViewModel.isTimeEnabled)
                                        putExtra(IntentKeys.EXTRA_BORDERS_MODE, mainViewModel.isBordersMode)
                                        putExtra(IntentKeys.EXTRA_REVERSE_MODE, mainViewModel.isReverseMode)
                                    }
                                    startActivity(intent)
                                } else {
                                    startActivity(Intent(this@MainActivity, ConfigurationActivity::class.java))
                                }
                            },
                            onConsult = { startActivity(Intent(this@MainActivity, ConsultActivity::class.java)) },
                            onHelp    = { startActivity(Intent(this@MainActivity, HelpActivity::class.java)) },
                            onExit    = { finishAffinity() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit,
    onConsult: () -> Unit,
    onHelp: () -> Unit,
    onExit: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(AnimationConfig.INITIAL_START_DELAY); visible = true }

    val infiniteTransition = rememberInfiniteTransition(label = "title_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationConfig.DURATION_LONG, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                this@Row.AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL)) + slideInHorizontally(tween(AnimationConfig.DURATION_NORMAL, easing = EaseOutCubic)) { -80 }
                ) { MenuTitle(glowAlpha) }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                this@Row.AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_MEDIUM)) + slideInHorizontally(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_MEDIUM, easing = EaseOutCubic)) { 80 }
                ) { MenuButtons(onStartGame, onConsult, onHelp, onExit) }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL, easing = EaseOutCubic)) { -80 }
            ) { MenuTitle(glowAlpha) }

            Spacer(Modifier.height(40.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_MEDIUM)) + slideInVertically(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_MEDIUM, easing = EaseOutCubic)) { 60 }
            ) { MenuButtons(onStartGame, onConsult, onHelp, onExit) }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL, AnimationConfig.DELAY_LONG))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i -> MiniCardDecoration(delay = i * AnimationConfig.DELAY_SHORT) }
                }
            }
        }
    }
}

@Composable
fun MenuTitle(glowAlpha: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalDividerWithDiamonds()
        Spacer(Modifier.height(24.dp))
        Text(text = stringResource(R.string.menu_title_top), fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = 12.sp, color = TtTextPrimary, style = LocalTextStyle.current.copy(shadow = Shadow(color = TtBluePrimary.copy(alpha = glowAlpha), blurRadius = 24f)))
        Text(text = stringResource(R.string.menu_title_bottom), fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = 12.sp, color = TtGoldLight, style = LocalTextStyle.current.copy(shadow = Shadow(color = TtGold.copy(alpha = glowAlpha), blurRadius = 28f)))
        Spacer(Modifier.height(8.dp))
        HorizontalDividerWithDiamonds()
    }
}

@Composable
fun MenuButtons(onStartGame: () -> Unit, onConsult: () -> Unit, onHelp: () -> Unit, onExit: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MenuButton(label = stringResource(R.string.menu_new_game), icon = stringResource(R.string.menu_new_game_icon), isPrimary = true, onClick = onStartGame)
        MenuButton(label = stringResource(R.string.menu_consult), icon = stringResource(R.string.menu_consult_icon), isPrimary = false, onClick = onConsult)
        MenuButton(label = stringResource(R.string.menu_help), icon = stringResource(R.string.menu_help_icon), isPrimary = false, onClick = onHelp)
        MenuButton(label = stringResource(R.string.menu_exit), icon = stringResource(R.string.menu_exit_icon), isPrimary = false, onClick = onExit)
    }
}

@Composable
fun MenuButton(label: String, icon: String, isPrimary: Boolean, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow), label = "scale")
    val borderColor = if (isPrimary) TtGold else TtBorder
    val bgColor     = if (isPrimary) TtBluePrimary.copy(alpha = 0.15f) else TtBgSurface.copy(alpha = 0.6f)
    val textColor   = if (isPrimary) TtGoldLight else TtTextSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .scale(scale)
            .border(width = if (isPrimary) 1.5.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
            .background(bgColor, RoundedCornerShape(4.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { pressed = true; onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, color = borderColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = label, fontSize = 14.sp, letterSpacing = 3.sp, fontWeight = FontWeight.SemiBold, color = textColor)
        }
    }
    LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }
}

@Composable
fun MiniCardDecoration(delay: Int = 0) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationConfig.DURATION_LONG + delay, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "float")
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 56.dp)
            .offset(y = offsetY.dp)
            .border(1.dp, TtBluePrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .background(Brush.verticalGradient(listOf(TtBgCard, TtBgDeep)), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center) {
        Text("?", color = TtBlueLight.copy(alpha = 0.4f), fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}