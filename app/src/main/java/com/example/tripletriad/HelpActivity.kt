package com.example.tripletriad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripletriad.ui.theme.TripleTriadTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HelpScreen(
                        onBack = {
                            // Esto destruye la pantalla de ayuda y te devuelve al menú
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AYUDA - TRIPLE TRIAD",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "El Triple Triad se juega en un tablero de 3x3.\n\n" +
                    "Cada jugador coloca cartas por turnos. Si el valor de tu carta " +
                    "es mayor que el de la carta adyacente del oponente, la capturas " +
                    "y cambia a tu color.\n\n" +
                    "Gana el jugador que tenga más cartas de su color cuando el tablero se llene.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBack) {
            Text("Volver al Menú")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HelpScreen(
                onBack = { } // Dejamos la acción vacía para el preview
            )
        }
    }
}