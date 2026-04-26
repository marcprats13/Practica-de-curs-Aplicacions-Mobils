package com.example.tripletriad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                            val intent = Intent(this, GameActivity::class.java).apply {
                                putExtra(IntentKeys.EXTRA_ALIAS, alias)
                                putExtra(IntentKeys.EXTRA_TIME_CONTROL, isTimeEnabled)
                                putExtra(IntentKeys.EXTRA_BORDERS_MODE, isBorders)
                                putExtra(IntentKeys.EXTRA_REVERSE_MODE, isReverse)
                            }
                            startActivity(intent)
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
    var alias by remember { mutableStateOf("") }
    var isAliasError by remember { mutableStateOf(false) }
    var isTimeEnabled by remember { mutableStateOf(false) }
    var isBordersMode by remember { mutableStateOf(false) }
    var isReverseMode by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CONFIGURACIÓN DE PARTIDA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Campo para el Alias
        OutlinedTextField(
            value = alias,
            onValueChange = {
                alias = it
                if (it.isNotBlank()) {
                    isAliasError = false
                }
            },
            label = { Text("Alias del jugador") },
            isError = isAliasError,
            supportingText = {
                if (isAliasError) {
                    Text(
                        text = "Alias obligatorio para empezar partida",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Control de Tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Control de Tiempo (120s)")
            Switch(checked = isTimeEnabled, onCheckedChange = { isTimeEnabled = it })
        }

        // Modo Fronteras
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

        // Modo Inverso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Modo Inverso")
                Text("El número menor captura al mayor", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = isReverseMode, onCheckedChange = { isReverseMode = it })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (alias.isNotBlank()) {
                    onStartGame(alias, isTimeEnabled, isBordersMode, isReverseMode)
                } else {
                    isAliasError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar Partida")
        }
    }
}
