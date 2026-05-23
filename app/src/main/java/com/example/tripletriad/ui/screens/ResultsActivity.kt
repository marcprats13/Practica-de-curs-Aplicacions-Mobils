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
            timedOut -> "Tiempo agotado"
            p1 > opp -> "Victoria"
            p1 < opp -> "Derrota"
            else     -> "Empate"
        }

        val logResumen = """
            RESUMEN DE LA PARTIDA
            Alias: $alias
            Mida Parrilla: ${size}x${size}
            Tiempo empleado: $time segundos
            Resultado: ${
            when {
                timedOut -> "Derrota (tiempo agotado)"
                p1 > opp -> "Victoria"
                p1 < opp -> "Derrota"
                else -> "Empate"
            } } ($p1 - $opp)
            Finalizado el: $now
            Modo Fronteras: ${if (borders) "Activado" else "Desactivado"}
            Modo Inverso: ${if (reverse) "Activado" else "Desactivado"}
        """.trimIndent()

        setContent {
            TripleTriadTheme {
                val viewModel: ResultsViewModel = viewModel()
                val partidaViewModel: PartidaViewModel = viewModel(
                    factory = PartidaViewModelFactory((application as GameApplication).repository)
                )

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
                        TopAppBar(
                            title = {},
                            actions = {
                                IconButton(
                                    onClick = { startActivity(Intent(this@ResultsActivity, ConfigurationActivity::class.java)) }
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
                                startActivity(Intent.createChooser(intentEmail, getString(R.string.results_chooser)))
                            },
                            onPlayAgain = {
                                startActivity(Intent(this@ResultsActivity, ConfigurationActivity::class.java))
                                finish()
                            },
                            onMainMenu = {
                                startActivity(Intent(this@ResultsActivity, MainActivity::class.java))
                                finish()
                            }
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
    onMainMenu: () -> Unit
) {
    // Aquí col·locaries el disseny que tinguis implementat per pintar les dades de la teva pantalla de resultats
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "PARTIDA FINALIZADA", fontSize = 24.sp, color = TtTextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "$playerName vs ENEMIGO", fontSize = 18.sp, color = TtGold)
            Text(text = "$p1Score - $oppScore", fontSize = 32.sp, color = TtTextPrimary, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onPlayAgain, colors = ButtonDefaults.buttonColors(containerColor = TtBluePrimary)) {
                Text("Volver a jugar", color = TtTextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onMainMenu, border = BorderStroke(1.dp, TtBorder)) {
                Text("Menú Principal", color = TtTextSecondary)
            }
        }
    }
}