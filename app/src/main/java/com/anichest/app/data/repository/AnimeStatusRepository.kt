package com.anichest.app.data.repository

import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * アニメ視聴状況データアクセスを抽象化するRepository
 */
class AnimeStatusRepository(private val animeStatusDao: AnimeStatusDao) {

    fun getAllStatus(): Flow<List<AnimeStatus>> = animeStatusDao.getAllStatus()

    fun getStatusByWatchStatus(status: WatchStatus): Flow<List<AnimeStatus>> =
        animeStatusDao.getStatusByWatchStatus(status)

    suspend fun getStatusByAnimeId(animeId: Int): AnimeStatus? =
        animeStatusDao.getStatusByAnimeId(animeId)

    suspend fun insertOrUpdateStatus(status: AnimeStatus) =
        animeStatusDao.insertOrUpdateStatus(status)

    suspend fun updateStatus(status: AnimeStatus) = animeStatusDao.updateStatus(status)

    suspend fun deleteStatus(status: AnimeStatus) = animeStatusDao.deleteStatus(status)

    suspend fun deleteStatusByAnimeId(animeId: Int) = animeStatusDao.deleteStatusByAnimeId(animeId)

    fun getWatchingCount(): Flow<Int> = animeStatusDao.getWatchingCount()

    fun getCompletedCount(): Flow<Int> = animeStatusDao.getCompletedCount()
}