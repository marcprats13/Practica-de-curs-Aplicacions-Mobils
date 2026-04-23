package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        onEmpezarClick = { alias, size, isTimeEnabled ->
                            // 1. Preparamos el Intent explícito hacia el Juego
                            val intent = Intent(this, GameActivity::class.java).apply {
                                // 2. Pasamos los datos introducidos
                                putExtra("EXTRA_ALIAS", alias)
                                putExtra("EXTRA_SIZE", size)
                                putExtra("EXTRA_TIME_CONTROL", isTimeEnabled)
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
fun ConfiguracionScreen(onEmpezarClick: (String, Int, Boolean) -> Unit) {
    // Definición de los estados que recordarán lo que escribe el usuario
    var alias by remember { mutableStateOf("Jugador1") }
    var gridSizeText by remember { mutableStateOf("3") }
    var controlTiempo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CONFIGURACIÓN DE PARTIDA", style = MaterialTheme.typography.titleLarge)
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

        // Campo para el Tamaño de la Parrilla
        OutlinedTextField(
            value = gridSizeText,
            onValueChange = { gridSizeText = it },
            label = { Text("Tamaño de la parrilla (ej. 3 para 3x3)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox para el Control del Tiempo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = controlTiempo,
                onCheckedChange = { controlTiempo = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Habilitar control de tiempo")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Parseamos el texto a Int (si falla o está vacío, le ponemos 3 por defecto para evitar cuelgues)
                val size = gridSizeText.toIntOrNull() ?: 3
                onEmpezarClick(alias, size, controlTiempo)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar Partida")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfiguracionPreview() {
    MaterialTheme {
        ConfiguracionScreen(onEmpezarClick = { _, _, _ -> })
    }
}