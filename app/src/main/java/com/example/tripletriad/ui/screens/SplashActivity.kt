package com.example.tripletriad.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.tripletriad.R
import com.example.tripletriad.ui.theme.*
import kotlinx.coroutines.delay
import com.example.tripletriad.utils.AnimationConfig

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TtBgDeep
                ) {
                    SplashScreen(
                        onFinished = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // Estado de animación
    var showLogo    by remember { mutableStateOf(false) }
    var showTitle   by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200);  showLogo    = true
        delay(600);  showTitle   = true
        delay(300);  showLoading = true
        delay(1200); onFinished()
    }

    // El glow pulsante
    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )
    val cardScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "card_scale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        MenuBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Carta grande animada
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(tween(800)) + scaleIn(tween(800, easing = EaseOutCubic))
            ) {
                Box(
                    modifier = Modifier
                        .scale(cardScale)
                        .size(width = 120.dp, height = 152.dp)
                        .border(
                            2.dp,
                            TtBluePrimary.copy(alpha = glowAlpha),
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                listOf(TtBluePrimary.copy(alpha = 0.3f), TtBgDeep)
                            ),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Valores de la carta
                    Text(
                        text = stringResource(R.string.splash_card_top),
                        color = TtBlueLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
                    )
                    Text(
                        text = stringResource(R.string.splash_card_bottom),
                        color = TtBlueLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp)
                    )
                    Text(
                        text = stringResource(R.string.splash_card_left),
                        color = TtBlueLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp)
                    )
                    Text(
                        text = stringResource(R.string.splash_card_right),
                        color = TtBlueLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)
                    )
                    // Diamante central
                    Text(
                        text = stringResource(R.string.splash_card_center),
                        color = TtGoldLight,
                        fontSize = 32.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(
                                color = TtGold.copy(alpha = glowAlpha),
                                blurRadius = 20f
                            )
                        )
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Titulo
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(AnimationConfig.DURATION_NORMAL)) + slideInVertically(tween(
                    AnimationConfig.DURATION_NORMAL, easing = EaseOutCubic)) { 40 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalDividerWithDiamonds()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "TRIPLE",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 10.sp,
                        color = TtTextPrimary,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(
                                color = TtBluePrimary.copy(alpha = glowAlpha),
                                blurRadius = 24f
                            )
                        )
                    )
                    Text(
                        text = "TRIAD",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 10.sp,
                        color = TtGoldLight,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(
                                color = TtGold.copy(alpha = glowAlpha),
                                blurRadius = 28f
                            )
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDividerWithDiamonds()
                }
            }


            Spacer(Modifier.height(76.dp))

            // Animación de loading
            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn(tween(AnimationConfig.DURATION_FAST))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i ->
                        LoadingDot(delayMs = i * 200)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingDot(delayMs: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot_$delayMs")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delayMs, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(TtGold.copy(alpha = alpha), RoundedCornerShape(4.dp))
    )
}