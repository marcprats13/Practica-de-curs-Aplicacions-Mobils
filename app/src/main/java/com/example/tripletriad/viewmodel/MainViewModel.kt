package com.example.tripletriad.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripletriad.data.DataStoreManager
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    var alias by mutableStateOf("")
    var isTimeEnabled by mutableStateOf(false)
    var isBordersMode by mutableStateOf(false)
    var isReverseMode by mutableStateOf(false)

    init {
        viewModelScope.launch { dataStoreManager.aliasFlow.collect { alias = it } }
        viewModelScope.launch { dataStoreManager.timeEnabledFlow.collect { isTimeEnabled = it } }
        viewModelScope.launch { dataStoreManager.bordersModeFlow.collect { isBordersMode = it } }
        viewModelScope.launch { dataStoreManager.reverseModeFlow.collect { isReverseMode = it } }
    }

    // Si los alias estan vacios
    fun hasPreferences(): Boolean {
        return alias.isNotBlank()
    }
}