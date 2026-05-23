package com.example.tripletriad.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensió per instanciar el DataStore de manera única com a Singleton en el Context de l'App
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        // Definim les claus per a cada propietat que volem desar
        val ALIAS_KEY = stringPreferencesKey("user_alias")
        val TIME_ENABLED_KEY = booleanPreferencesKey("time_enabled")
        val BORDERS_MODE_KEY = booleanPreferencesKey("borders_mode")
        val REVERSE_MODE_KEY = booleanPreferencesKey("reverse_mode")
    }

    // Fluxos (Flows) per llegir els valors. Si no existeixen, retornem valors per defecte.
    val aliasFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ALIAS_KEY] ?: ""
    }

    val timeEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[TIME_ENABLED_KEY] ?: false
    }

    val bordersModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BORDERS_MODE_KEY] ?: false
    }

    val reverseModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REVERSE_MODE_KEY] ?: false
    }

    // Funcions suspeses per desar les preferències
    suspend fun savePreferences(alias: String, isTimeEnabled: Boolean, isBorders: Boolean, isReverse: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALIAS_KEY] = alias
            prefs[TIME_ENABLED_KEY] = isTimeEnabled
            prefs[BORDERS_MODE_KEY] = isBorders
            prefs[REVERSE_MODE_KEY] = isReverse
        }
    }
}