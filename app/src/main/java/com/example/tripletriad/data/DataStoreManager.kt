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

// Extensión para instanciar el DataStore de forma única como Singleton en el Contexto de la App
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        // Definimos las claves para cada propiedad que queremos guardar
        val ALIAS_KEY = stringPreferencesKey("user_alias")
        val TIME_ENABLED_KEY = booleanPreferencesKey("time_enabled")
        val BORDERS_MODE_KEY = booleanPreferencesKey("borders_mode")
        val REVERSE_MODE_KEY = booleanPreferencesKey("reverse_mode")
    }

    // Flows para leer los valores. Si no existen, devolvemos valores por defecto.
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

    // Funciones suspendidas para guardar las preferencias
    suspend fun savePreferences(alias: String, isTimeEnabled: Boolean, isBorders: Boolean, isReverse: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALIAS_KEY] = alias
            prefs[TIME_ENABLED_KEY] = isTimeEnabled
            prefs[BORDERS_MODE_KEY] = isBorders
            prefs[REVERSE_MODE_KEY] = isReverse
        }
    }
}