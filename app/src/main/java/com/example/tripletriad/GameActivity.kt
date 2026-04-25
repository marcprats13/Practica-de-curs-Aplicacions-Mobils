package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.ui.theme.TripleTriadTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperamos los datos del Intent
        val playerName = intent.getStringExtra("EXTRA_ALIAS") ?: "Player 1"
        val gridSize = intent.getIntExtra("EXTRA_SIZE", 3)
        val isTimeEnabled = intent.getBooleanExtra("EXTRA_TIME_CONTROL", false)

        setContent {
            TripleTriadTheme {
                // Instanciamos el ViewModel
                val gameViewModel: GameViewModel = viewModel()

                // Timer
                LaunchedEffect(Unit) {
                    if (isTimeEnabled) {
                        gameViewModel.startTimer(50)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Dibujamos la pantalla normal del juego
                    GameScreen(playerName, gridSize, isTimeEnabled, gameViewModel)

                    // 2. LA ALERTA DE FIN DE JUEGO (Pop-up)
                    if (gameViewModel.isGameOver) {
                        val timeSpent = if (isTimeEnabled) 25 - gameViewModel.timeLeft else 0

                        // Calculamos el texto de victoria/derrota para el pop-up
                        val resultMessage = when {
                            gameViewModel.playerScore > gameViewModel.opponentScore -> "¡VICTORIA!"
                            gameViewModel.playerScore < gameViewModel.opponentScore -> "¡DERROTA!"
                            else -> "¡EMPATE!"
                        }

                        AlertDialog(
                            onDismissRequest = { /* Lo dejamos vacío para obligar al jugador a pulsar el botón */ },
                            title = {
                                Text(
                                    text = "Partida Finalizada",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = resultMessage,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Tú: ${gameViewModel.playerScore} - Máquina: ${gameViewModel.opponentScore}")
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    // AQUÍ ESTÁ EL SALTO A RESULTADOS
                                    val intent = Intent(
                                        this@GameActivity,
                                        ResultsActivity::class.java
                                    ).apply {
                                        putExtra("EXTRA_NAME", playerName)
                                        putExtra("EXTRA_SIZE", gridSize)
                                        putExtra("EXTRA_P1_SCORE", gameViewModel.playerScore)
                                        putExtra("EXTRA_OPP_SCORE", gameViewModel.opponentScore)
                                        putExtra("EXTRA_TIME", timeSpent)
                                    }
                                    startActivity(intent)
                                    finish() // Cerramos GameActivity
                                }) {
                                    Text("Ver Resultados y Enviar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameScreen(playerName: String, gridSize : Int, isTimeEnabled: Boolean, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // <--- ¡AQUÍ ESTÁ LA MAGIA!
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Temporizador
        Text(text = "$playerName's Game", style = MaterialTheme.typography.titleLarge)

        if (isTimeEnabled) {
            Text(
                text = "Tiempo Restante: ${viewModel.timeLeft} s",
                style = MaterialTheme.typography.bodyLarge,
                // Si quedan 5s o menos, se pone en rojo
                color = if (viewModel.timeLeft <= 5) Color.Red else Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Marcadores de la partida
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("You: ${viewModel.playerScore}", color = Color.Blue, fontWeight = FontWeight.Bold)
            Text("Turn: ${if (viewModel.isPlayer1Turn) "Yours" else "Opponent's"}")
            Text("Opponent: ${viewModel.opponentScore}", color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TABLERO 3x3
        Box(modifier = Modifier.size(300.dp).border(2.dp, Color.Black)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columnas para el 3x3
                modifier = Modifier.fillMaxSize()
            ) {
                items(9) { index ->
                    BoardCell(
                        card = viewModel.board[index],
                        onClick = {
                            viewModel.playCard(index)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // MOSTRAR LA MANO DEL JUGADOR
        Text(text = "Your Hand", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            viewModel.playerHand.forEach { card ->
                val isSelected = card == viewModel.selectedCard

                Box(
                    modifier = Modifier
                        .clickable { viewModel.selectCard(card) } // Le avisamos al ViewModel que la hemos tocado
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color.Yellow else Color.Transparent
                        )
                ) {
                    CardView(card, MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun BoardCell(card: Card?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Células cuadradas
            .border(1.dp, Color.Gray)
            .clickable { onClick() }
            .background(if (card == null) Color.White else Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (card != null) {
            // Si la carta es nuestra Verde i si es enemiga Roja
            val cellColor = when (card?.owner) {
                Player.PLAYER_1 -> MaterialTheme.colorScheme.primary   // Esto ahora será VERDE
                Player.OPPONENT -> MaterialTheme.colorScheme.secondary // Esto ahora será ROJO
                else -> Color.LightGray
            }
            CardView(card, cellColor)
        }
    }
}

@Composable
fun CardView(card: Card, backgroundColor: Color) {
    // Una representación simple de la carta con sus 4 números
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(60.dp, 80.dp)
            .background(backgroundColor)
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = card.top.toString(), fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = " "+card.left.toString(), fontWeight = FontWeight.Bold)
                Text(text = card.right.toString()+" ", fontWeight = FontWeight.Bold)
            }
            Text(text = card.bottom.toString(), fontWeight = FontWeight.Bold)
        }
    }
}