package com.example.tripletriad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.ui.theme.TripleTriadTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperamos los datos del Intent
        val alias = intent.getStringExtra("EXTRA_ALIAS") ?: "Player"
        val isTimeEnabled = intent.getBooleanExtra("EXTRA_TIME_CONTROL", false)

        setContent {
            TripleTriadTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Conectamos el ViewModel
                    val gameViewModel: GameViewModel = viewModel()

                    GameScreen(alias, isTimeEnabled, gameViewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(alias: String, isTimeEnabled: Boolean, viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Información de la partida
        Text(text = "Player: $alias", style = MaterialTheme.typography.headlineSmall)
        Text(text = if (viewModel.isPlayer1Turn) "Your Turn" else "Opponent's Turn")

        Spacer(modifier = Modifier.height(24.dp))

        // TABLERO 3x3 (Parrilla)
        // Usamos LazyVerticalGrid para crear la cuadrícula del tablero
        Box(modifier = Modifier.size(300.dp).border(2.dp, Color.Black)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columnas para el 3x3
                modifier = Modifier.fillMaxSize()
            ) {
                items(9) { index ->
                    BoardCell(
                        card = viewModel.board[index],
                        onClick = {
                            // Por ahora, si tenemos cartas en la mano, jugamos la primera
                            if (viewModel.playerHand.isNotEmpty()) {
                                viewModel.playCard(index, viewModel.playerHand[0])
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // MOSTRAR LA MANO DEL JUGADOR
        Text(text = "Your Hand", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.playerHand.forEach { card ->
                CardView(card)
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
            CardView(card)
        }
    }
}

@Composable
fun CardView(card: Card) {
    // Una representación simple de la carta con sus 4 números
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = card.top.toString(), style = MaterialTheme.typography.labelSmall)
        Row {
            Text(text = card.left.toString(), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = card.right.toString(), style = MaterialTheme.typography.labelSmall)
        }
        Text(text = card.bottom.toString(), style = MaterialTheme.typography.labelSmall)
    }
}