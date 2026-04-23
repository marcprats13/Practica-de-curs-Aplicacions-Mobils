package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripletriad.ui.theme.TripleTriadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainMenuScreen(
                        onHelp = {
                            // Intent Explícito hacia Ayuda
                            val intent = Intent(this, HelpActivity::class.java)
                            startActivity(intent)
                        },
                        onStartGame = {
                            // Intent Explícito hacia Configuración
                            val intent = Intent(this, ConfigurationActivity::class.java)
                            startActivity(intent)
                        },
                        onExit = {
                            finishAffinity() // Cierra la app y limpia toda la pila de golpe
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    onHelp: () -> Unit,
    onStartGame: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "TRIPLE TRIAD", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onHelp) {
            Text("Ayuda")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onStartGame) {
            Text("Empezar Partida")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onExit) {
            Text("Salir")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainMenuScreen(
                onHelp = {},
                onStartGame = {},
                onExit = {}
            )
        }
    }
}