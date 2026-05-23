package com.example.tripletriad.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripletriad.GameApplication
import com.example.tripletriad.R
import com.example.tripletriad.data.PartidaEntity
import com.example.tripletriad.ui.theme.*
import com.example.tripletriad.viewmodel.PartidaViewModel
import com.example.tripletriad.viewmodel.PartidaViewModelFactory

class ConsultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleTriadTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = TtBgDeep) {

                    val partidaViewModel: PartidaViewModel = viewModel(
                        factory = PartidaViewModelFactory(
                            (application as GameApplication).repository
                        )
                    )

                    ConsultScreen(
                        viewModel = partidaViewModel,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun ConsultScreen(
    viewModel: PartidaViewModel,
    onBack: () -> Unit
) {
    // Recoge el Flow de partidas de forma consciente del ciclo de vida.
    val partidas by viewModel.allPartidas.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var partidaSeleccionada by remember { mutableStateOf<PartidaEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.consult_game),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            color = TtGoldLight
        )
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = TtBorder)
        Spacer(Modifier.height(12.dp))

        val seleccion = partidaSeleccionada

        if (seleccion != null) {
            Box(modifier = Modifier.weight(1f)) {
                DetailReg(seleccion)
            }
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { partidaSeleccionada = null },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TtPlayerBlue
                )
            ) {
                Text(stringResource(R.string.consult_return))
            }
        } else {
            if (partidas.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.consult_no_game),
                        color = TtTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(partidas) { partida ->
                        PartidaItem(
                            partida = partida,
                            onClick = { partidaSeleccionada = partida }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TtPlayerBlue
                )
            ) {
                Text(stringResource(R.string.consult_menu))
            }
        }
    }
}

// Una fila de la lista
@Composable
fun PartidaItem(partida: PartidaEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
            .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = partida.alias,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TtTextPrimary
        )
        Text(
            text = partida.fechaHora,
            fontSize = 12.sp,
            color = TtTextSecondary
        )
        Text(
            text = partida.resultado,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TtGold
        )
    }
}

// Detalle completo de una partida
@Composable
fun DetailReg(partida: PartidaEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TtBorder, RoundedCornerShape(6.dp))
            .background(TtBgSurface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.consult_detail),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            color = TtGold
        )
        HorizontalDivider(color = TtBorder)

        DetailRow("Alias", partida.alias)
        DetailRow("Fecha y hora", partida.fechaHora)
        DetailRow("Tamaño parrilla", "${partida.tamParrilla}x${partida.tamParrilla}")
        DetailRow("Modo fronteras", if (partida.modoFronteras) "Activado" else "Desactivado")
        DetailRow("Modo inverso", if (partida.modoInverso) "Activado" else "Desactivado")
        DetailRow("Tiempo empleado", "${partida.tiempoEmpleado} segundos")
        DetailRow("Puntos jugador", partida.puntosJugador.toString())
        DetailRow("Puntos enemigo", partida.puntosEnemigo.toString())
        DetailRow("Resultado", partida.resultado)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TtTextSecondary)
        Text(value, fontSize = 12.sp, color = TtTextPrimary, fontWeight = FontWeight.SemiBold)
    }
}