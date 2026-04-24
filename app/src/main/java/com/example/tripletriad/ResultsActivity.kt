package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tripletriad.ui.theme.TripleTriadTheme

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperamos los puntos y el nombre
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Player"
        val p1Score = intent.getIntExtra("EXTRA_P1_SCORE", 0)
        val oppScore = intent.getIntExtra("EXTRA_OPP_SCORE", 0)

        setContent {
            TripleTriadTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ResultsScreen(
                        name = name,
                        p1Score = p1Score,
                        oppScore = oppScore,
                        onPlayAgain = {
                            // Volvemos a la configuración
                            startActivity(Intent(this, ConfigurationActivity::class.java))
                            finish()
                        },
                        onMainMenu = {
                            // Volvemos al menú principal limpiando la pila
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(name: String, p1Score: Int, oppScore: Int, onPlayAgain: () -> Unit, onMainMenu: () -> Unit) {
    val resultText = when {
        p1Score > oppScore -> "¡HAS GANADO, $name!"
        p1Score < oppScore -> "HAS PERDIDO..."
        else -> "¡EMPATE!"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = resultText, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Puntuación Final", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Tú: $p1Score - Oponente: $oppScore", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onPlayAgain) { Text("Jugar otra vez") }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onMainMenu) { Text("Menú Principal") }
    }
}