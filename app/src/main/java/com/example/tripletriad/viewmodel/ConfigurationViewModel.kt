package com.example.tripletriad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConfigurationViewModel : ViewModel() {
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
}