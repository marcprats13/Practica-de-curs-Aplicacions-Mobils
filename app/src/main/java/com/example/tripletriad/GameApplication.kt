package com.example.tripletriad

import android.app.Application
import com.example.tripletriad.data.GameDatabase
import com.example.tripletriad.data.PartidaRepository

class GameApplication : Application() {

    // Se crea database y repository la primera vez que se usan
    val database by lazy { GameDatabase.getDatabase(this) }
    val repository by lazy { PartidaRepository(database.partidaDao()) }
}