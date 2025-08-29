package com.anichest.app.data.repository

import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeWithStatus
import kotlinx.coroutines.flow.Flow

/**
 * アニメデータアクセスを抽象化するRepository
 */
class AnimeRepository(private val animeDao: AnimeDao) {

    fun getAllAnime(): Flow<List<Anime>> = animeDao.getAllAnime()

    fun getAllAnimeWithStatus(): Flow<List<AnimeWithStatus>> = animeDao.getAllAnimeWithStatus()

    fun searchAnimeByTitle(query: String): Flow<List<Anime>> = animeDao.searchAnimeByTitle(query)

    fun getAnimeByStatus(status: String): Flow<List<AnimeWithStatus>> =
        animeDao.getAnimeByStatus(status)

    suspend fun getAnimeById(id: Long): Anime? = animeDao.getAnimeById(id)

    suspend fun insertAnime(anime: Anime): Long = animeDao.insertAnime(anime)

    suspend fun updateAnime(anime: Anime) = animeDao.updateAnime(anime)

    suspend fun deleteAnime(anime: Anime) = animeDao.deleteAnime(anime)

    suspend fun deleteAnimeById(id: Long) = animeDao.deleteAnimeById(id)
}
