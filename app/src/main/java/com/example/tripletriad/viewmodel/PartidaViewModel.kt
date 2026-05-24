package com.example.tripletriad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripletriad.data.PartidaEntity
import com.example.tripletriad.data.PartidaRepository
import kotlinx.coroutines.launch


class PartidaViewModel(private val repository: PartidaRepository) : ViewModel() {

    // Flow del repositorio con todas las partidas.
    val allPartidas = repository.allPartidas

    // Para indicar si la partida de x pantalla ya se ha guardado
    private var partidaGuardada = false

    // Coroutine para insertar sin bloquear la interficie
    fun insert(partida: PartidaEntity) {
        if (partidaGuardada) return
        partidaGuardada = true
        viewModelScope.launch {
            repository.insert(partida)
        }
    }
}

// Factory por el parámetro del constructor.
class PartidaViewModelFactory(
    private val repository: PartidaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PartidaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PartidaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}