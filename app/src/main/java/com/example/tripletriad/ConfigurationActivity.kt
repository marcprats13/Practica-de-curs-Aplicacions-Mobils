package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripletriad.ui.theme.TripleTriadTheme

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConfiguracionScreen(
                        onStartGame = { alias, isTimeEnabled, isBorders, isReverse ->
                            // 1. Preparamos el Intent explícito hacia el Juego
                            val intent = Intent(this, GameActivity::class.java).apply {
                                // 2. Pasamos los datos introducidos
                                putExtra("EXTRA_ALIAS", alias)
                                putExtra("EXTRA_SIZE", 3)
                                putExtra("EXTRA_TIME_CONTROL", isTimeEnabled)
                                putExtra("EXTRA_BORDERS_MODE", isBorders)
                                putExtra("EXTRA_REVERSE_MODE", isReverse)
                            }
                            // 3. Iniciamos la actividad
                            startActivity(intent)

                            // 4. MATAMOS esta actividad para que no quede en el Back Stack!!
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConfiguracionScreen(onStartGame: (String, Boolean, Boolean, Boolean) -> Unit) {
    // Definición de los estados que recordarán lo que escribe el usuario
    var alias by remember { mutableStateOf("Jugador1") }
    var isTimeEnabled by remember { mutableStateOf(false) }
    var isBordersMode by remember { mutableStateOf(false) }
    var isReverseMode by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CONFIGURACIÓN DE PARTIDA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Campo para el Alias
        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("Alias del jugador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Control de Tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Control de Tiempo (25s)")
            Switch(checked = isTimeEnabled, onCheckedChange = { isTimeEnabled = it })
        }

        // 3. Modo Fronteras
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Modo Fronteras")
                Text("El tablero se conecta por los bordes", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = isBordersMode, onCheckedChange = { isBordersMode = it })
        }

        // 4. Modo Inverso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Modo Inverso")
                Text("El número MENOR captura al mayor", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = isReverseMode, onCheckedChange = { isReverseMode = it })
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón Jugar con Validación
        Button(
            onClick = {
                if (alias.isNotBlank()) {
                    onStartGame(alias, isTimeEnabled, isBordersMode, isReverseMode)
                } else {
                    Toast.makeText(context, "El Alias no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar Partida")
        }
    }
}
