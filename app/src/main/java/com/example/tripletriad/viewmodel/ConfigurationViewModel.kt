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
    fun setTimeEnabled(value: Boolean)  {
        isTimeEnabled = value
    }
    fun setBordersMode(value: Boolean)  {
        isBordersMode = value
    }
    fun setReverseMode(value: Boolean)  {
        isReverseMode = value
    }
    fun setAliasError(value: Boolean)   {
        isAliasError = value
    }
}