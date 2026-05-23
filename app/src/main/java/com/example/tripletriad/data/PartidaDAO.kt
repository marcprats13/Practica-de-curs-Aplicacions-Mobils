package com.example.tripletriad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    // Devuelve todas las partidas, las más recientes primero.
    // Al devolver Flow, Room ejecuta la query en un hilo de fondo
    // y notifica automáticamente a la UI cuando los datos cambian.
    @Query("SELECT * FROM partidas ORDER BY id DESC")
    fun getAllPartidas(): Flow<List<PartidaEntity>>

    // @Insert es una anotación de conveniencia: el compilador genera el SQL.
    // suspend  -> debe llamarse desde una corrutina (hilo de fondo).
    @Insert
    suspend fun insert(partida: PartidaEntity)
}