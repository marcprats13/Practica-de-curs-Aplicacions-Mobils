package com.example.tripletriad.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch

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
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ConsultScreen(
    viewModel: PartidaViewModel,
    onBack: () -> Unit
) {
    // Recoge el Flow de partidas.
    val partidas by viewModel.allPartidas.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    // Navigator del scaffold.
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    // Id de la partida seleccionada.
    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    val seleccion = partidas.find { it.id == selectedId }

    // Para preseleccionar la primera partida automaticamente
    LaunchedEffect(partidas) {
        if (selectedId == null && partidas.isNotEmpty()) {
            selectedId = partidas.first().id
        }
    }


    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        // Panel principal (listPane): la lista de partidas
        listPane = {
            AnimatedPane {
                ListaPartidasPane(
                    partidas = partidas,
                    onPartidaClick = { partida ->
                        selectedId = partida.id
                        // En móvil, navega al panel de detalle.
                        // En tablet, el panel de detalle ya es visible y solo se actualiza.
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    onBack = onBack
                )
            }
        },
        // Panel secundario
        detailPane = {
            AnimatedPane {
                if (seleccion != null) {
                    DetalleRegPane(seleccion,
                    mostrarVolver = navigator.canNavigateBack(),
                    onVolver = { scope.launch { navigator.navigateBack() } }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.consult_select),
                            color = TtTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    )
}

// Panel principal: título + lista de partidas + botón menú
@Composable
fun ListaPartidasPane(
    partidas: List<PartidaEntity>,
    onPartidaClick: (PartidaEntity) -> Unit,
    onBack: () -> Unit
) {
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
                        onClick = { onPartidaClick(partida) }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = TtPlayerBlue)
        ) {
            Text(stringResource(R.string.consult_menu))
        }
    }
}

// Panel secundario: detalle de partida
@Composable
fun DetalleRegPane(
    partida: PartidaEntity,
    mostrarVolver: Boolean,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DetailReg(partida)
        if (mostrarVolver) {
            Spacer(Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onVolver,
                colors = ButtonDefaults.buttonColors(containerColor = TtPlayerBlue)
            ) {
                Text(stringResource(R.string.consult_return))
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

        DetailRow(stringResource(R.string.detail_alias), partida.alias)
        DetailRow(stringResource(R.string.detail_datetime), partida.fechaHora)
        DetailRow(
            stringResource(R.string.detail_grid),
            "${partida.tamParrilla}x${partida.tamParrilla}"
        )
        DetailRow(
            stringResource(R.string.detail_borders),
            stringResource(if (partida.modoFronteras) R.string.detail_on else R.string.detail_off)
        )
        DetailRow(
            stringResource(R.string.detail_reverse),
            stringResource(if (partida.modoInverso) R.string.detail_on else R.string.detail_off)
        )
        DetailRow(
            stringResource(R.string.detail_time),
            stringResource(R.string.detail_seconds, partida.tiempoEmpleado)
        )
        DetailRow(stringResource(R.string.detail_player_points), partida.puntosJugador.toString())
        DetailRow(stringResource(R.string.detail_enemy_points), partida.puntosEnemigo.toString())
        DetailRow(stringResource(R.string.detail_result), partida.resultado)
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