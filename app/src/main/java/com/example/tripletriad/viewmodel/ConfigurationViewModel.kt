package com.example.tripletriad.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripletriad.data.DataStoreManager
import kotlinx.coroutines.launch

class ConfigurationViewModel(application: Application) : AndroidViewModel(application) {

    // Inicialitzem el gestor de persistència passant el context de l'aplicació
    private val dataStoreManager = DataStoreManager(application)

    var alias by mutableStateOf("")
        private set
    var isAliasError by mutableStateOf(false)
        private set
    var isTimeEnabled by mutableStateOf(false)
        private set
    var isBordersMode by mutableStateOf(false)
        private set
    var isReverseMode by mutableStateOf(false)
        private set

    init {
        // Carreguem de manera asíncrona les preferències desades en obrir la pantalla
        viewModelScope.launch {
            dataStoreManager.aliasFlow.collect { alias = it }
        }
        viewModelScope.launch {
            dataStoreManager.timeEnabledFlow.collect { isTimeEnabled = it }
        }
        viewModelScope.launch {
            dataStoreManager.bordersModeFlow.collect { isBordersMode = it }
        }
        viewModelScope.launch {
            dataStoreManager.reverseModeFlow.collect { isReverseMode = it }
        }
    }

    fun updateAlias(value: String) {
        alias = value
        if (value.isNotBlank()) isAliasError = false
    }
    fun updateTimeEnabled(value: Boolean)  {
        isTimeEnabled = value
    }
    fun updateBordersMode(value: Boolean)  {
        isBordersMode = value
    }
    fun updateReverseMode(value: Boolean)  {
        isReverseMode = value
    }

    fun isConfigValid(): Boolean {
        return if (alias.isBlank()) {
            isAliasError = true
            false
        } else {
            true
        }
    }

    // Desa l'estat actual al DataStore de forma persistent
    fun savePreferences() {
        viewModelScope.launch {
            dataStoreManager.savePreferences(
                alias = alias,
                isTimeEnabled = isTimeEnabled,
                isBorders = isBordersMode,
                isReverse = isReverseMode
            )
        }
    }
}