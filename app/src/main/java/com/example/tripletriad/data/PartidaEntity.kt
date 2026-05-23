package com.example.tripletriad.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "alias")
    val alias: String,

    // Día y hora de finalización de la partida
    @ColumnInfo(name = "fecha_hora")
    val fechaHora: String,

    @ColumnInfo(name = "tam_parrilla")
    val tamParrilla: Int,

    @ColumnInfo(name = "modo_fronteras")
    val modoFronteras: Boolean,

    @ColumnInfo(name = "modo_inverso")
    val modoInverso: Boolean,

    // Tiempo empleado en segundos
    @ColumnInfo(name = "tiempo_empleado")
    val tiempoEmpleado: Int,

    @ColumnInfo(name = "puntos_jugador")
    val puntosJugador: Int,

    @ColumnInfo(name = "puntos_enemigo")
    val puntosEnemigo: Int,

    // Resultado
    @ColumnInfo(name = "resultado")
    val resultado: String
)