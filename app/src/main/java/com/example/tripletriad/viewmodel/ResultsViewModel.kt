package com.example.tripletriad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tripletriad.utils.EmailConfig

class ResultsViewModel : ViewModel() {
    var emailRecipient by mutableStateOf(EmailConfig.DEFAULT_RECIPIENT)
        private set
    var emailSubject by mutableStateOf("")
        private set
    var logBody by mutableStateOf("")
        private set

    fun updateRecipient(value: String) {
        emailRecipient = value
    }
    fun updateSubject(value: String)   {
        emailSubject = value
    }
    fun updateLogBody(value: String)   {
        logBody = value
    }
    fun initData(subject: String, log: String) {
        if (emailSubject.isEmpty()) emailSubject = subject
        if (logBody.isEmpty()) logBody = log
    }
}