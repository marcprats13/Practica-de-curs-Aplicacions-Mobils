package com.example.tripletriad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConfigurationViewModel : ViewModel() {
    var alias by mutableStateOf("")
    var isAliasError by mutableStateOf(false)
    var isTimeEnabled by mutableStateOf(false)
    var isBordersMode by mutableStateOf(false)
    var isReverseMode by mutableStateOf(false)
}