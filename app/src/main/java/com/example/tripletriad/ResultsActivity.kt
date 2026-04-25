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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.example.tripletriad.ui.theme.TripleTriadTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Recuperamos los datos (Traspaso de datos - Tema 3 Parte III)
        val alias = intent.getStringExtra("EXTRA_NAME") ?: "Invitado"
        val size = intent.getIntExtra("EXTRA_SIZE", 3)
        val time = intent.getIntExtra("EXTRA_TIME", 0)
        val p1 = intent.getIntExtra("EXTRA_P1_SCORE", 0)
        val opp = intent.getIntExtra("EXTRA_OPP_SCORE", 0)

        // Formateo de fecha y hora (Estándar Kotlin/JVM)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val now = LocalDateTime.now().format(formatter)

        // Construcción del Log inicial
        val logResumen = """
            Alias: $alias
            Tamaño Parrilla: ${size}x${size}
            Tiempo empleado: $time segundos
            Resultado: Jugador $p1 - Máquina $opp
            Finalizado el: $now
        """.trimIndent()

        setContent {
            TripleTriadTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ResultsScreen(
                        initialLog = logResumen,
                        dateTime = now,
                        onSend = { email, subject, body ->
                            // 2. Intent Implícito para Email (Tema 3 Parte IV)
                            val intentEmail = Intent(Intent.ACTION_SEND).apply {
                                type = "message/rfc822"
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                                putExtra(Intent.EXTRA_SUBJECT, subject)
                                putExtra(Intent.EXTRA_TEXT, body)
                            }
                            startActivity(Intent.createChooser(intentEmail, "Enviar Log..."))
                        },
                        onPlayAgain = {
                            startActivity(Intent(this, ConfigurationActivity::class.java))
                            finish()
                        },
                        onExit = { finishAffinity() } // Cierra la App completa
                    )
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(
    initialLog: String,
    dateTime: String,
    onSend: (String, String, String) -> Unit,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    // Estados para que los campos sean editables (Requisito Rúbrica)
    var emailRecipient by remember { mutableStateOf("user@ejemplo.com") }
    var emailSubject by remember { mutableStateOf("$dateTime") }
    var logBody by remember { mutableStateOf(initialLog) }

    // Gestión del Foco (Requisito Rúbrica)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus() // Pone el foco en el email al entrar
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("RESULTADOS PARTIDA", style = MaterialTheme.typography.headlineMedium)

        // Email Destinatario (Con foco automático)
        OutlinedTextField(
            value = emailRecipient,
            onValueChange = { emailRecipient = it },
            label = { Text("Email Destinatario") },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
        )

        // Asunto Editable
        OutlinedTextField(
            value = emailSubject,
            onValueChange = { emailSubject = it },
            label = { Text("Asunto") },
            modifier = Modifier.fillMaxWidth()
        )

        // Log de la partida editable
        OutlinedTextField(
            value = logBody,
            onValueChange = { logBody = it },
            label = { Text("Log de la Partida") },
            modifier = Modifier.fillMaxWidth().height(180.dp)
        )

        Button(
            onClick = { onSend(emailRecipient, emailSubject, logBody) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Email con Log")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onPlayAgain, modifier = Modifier.weight(1f)) {
                Text("Nueva Partida")
            }
            OutlinedButton(onClick = onExit, modifier = Modifier.weight(1f)) {
                Text("Salir")
            }
        }
    }
}