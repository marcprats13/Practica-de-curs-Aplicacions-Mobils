package com.example.tripletriad.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PartidaEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    // "Getter" abstracto del DAO. Room genera la implementación automáticamente.
    abstract fun partidaDao(): PartidaDao

    companion object {
        // @Volatile -> los cambios en INSTANCE son visibles de inmediato a todos los hilos
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            // Si la instancia ya existe la devuelve;
            // si no, la crea de forma segura entre hilos
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                    // Si cambia el esquema sin migración, reconstruye la BBDD
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}