package com.example.tripletriad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tripletriad.utils.EmailConfig

class ResultsViewModel : ViewModel() {
    var emailRecipient by mutableStateOf(EmailConfig.DEFAULT_RECIPIENT)
    var emailSubject by mutableStateOf("")
    var logBody by mutableStateOf("")

    fun initData(subject: String, log: String) {
        if (emailSubject.isEmpty()) emailSubject = subject
        if (logBody.isEmpty()) logBody = log
    }
}