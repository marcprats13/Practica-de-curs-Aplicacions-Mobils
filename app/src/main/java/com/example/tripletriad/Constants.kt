package com.example.tripletriad

object IntentKeys {
    // Claves para el paso de datos
    const val EXTRA_ALIAS = "EXTRA_ALIAS"
    const val EXTRA_SIZE = "EXTRA_SIZE"
    const val EXTRA_TIME_CONTROL = "EXTRA_TIME_CONTROL"
    const val EXTRA_BORDERS_MODE = "EXTRA_BORDERS_MODE"
    const val EXTRA_REVERSE_MODE = "EXTRA_REVERSE_MODE"
    const val EXTRA_P1_SCORE = "EXTRA_P1_SCORE"
    const val EXTRA_OPP_SCORE = "EXTRA_OPP_SCORE"
    const val EXTRA_TIME_SPENT = "EXTRA_TIME_SPENT"
}

object GameSettings {
    // Configuración general del juego
    const val DEFAULT_GRID_SIZE = 3
    const val DEFAULT_TIME_SECONDS = 50
    const val AI_THINKING_DELAY = 1000L
}

object EmailConfig {
    const val MIME_TYPE = "message/rfc822"
    const val DEFAULT_RECIPIENT = "user@example.com"
}