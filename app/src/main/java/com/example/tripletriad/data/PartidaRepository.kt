package com.example.tripletriad.data

import kotlinx.coroutines.flow.Flow


class PartidaRepository(private val partidaDao: PartidaDao) {

    // Flow con todas las partidas.
    val allPartidas: Flow<List<PartidaEntity>> = partidaDao.getAllPartidas()


    suspend fun insert(partida: PartidaEntity) {
        partidaDao.insert(partida)
    }
}