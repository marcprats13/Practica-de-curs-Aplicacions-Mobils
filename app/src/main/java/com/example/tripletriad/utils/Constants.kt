package com.example.tripletriad.utils

object IntentKeys {
    const val EXTRA_ALIAS = "EXTRA_ALIAS"
    const val EXTRA_TIME_CONTROL = "EXTRA_TIME_CONTROL"
    const val EXTRA_BORDERS_MODE = "EXTRA_BORDERS_MODE"
    const val EXTRA_REVERSE_MODE = "EXTRA_REVERSE_MODE"
    const val EXTRA_P1_SCORE = "EXTRA_P1_SCORE"
    const val EXTRA_OPP_SCORE = "EXTRA_OPP_SCORE"
    const val EXTRA_TIME_SPENT = "EXTRA_TIME_SPENT"
}

object GameSettings {
    const val DEFAULT_GRID_SIZE = 3
    const val DEFAULT_TIME_SECONDS = 120
    const val AI_THINKING_DELAY = 1000L
}

object EmailConfig {
    const val MIME_TYPE = "message/rfc822"
    const val DEFAULT_RECIPIENT = "user@example.com"
}

object AnimationConfig {
    const val DURATION_NORMAL = 800
    const val DURATION_FAST = 600
    const val DURATION_LONG = 1800
    const val DURATION_LONG_2 = 8000
    const val DELAY_SHORT = 150
    const val DELAY_MEDIUM = 300
    const val DELAY_LONG = 500
    const val DELAY_LONG_2 = 650
    const val INITIAL_START_DELAY = 80L
}