package com.anichest.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * アニメ視聴状況データへのアクセスを提供するDAO
 */
@Dao
interface AnimeStatusDao {

    /**
     * 全ての視聴状況を取得
     */
    @Query("SELECT * FROM anime_status ORDER BY updatedAt DESC")
    fun getAllStatus(): Flow<List<AnimeStatus>>

    /**
     * 特定アニメの視聴状況を取得
     */
    @Query("SELECT * FROM anime_status WHERE animeId = :animeId")
    suspend fun getStatusByAnimeId(animeId: Long): AnimeStatus?

    /**
     * 特定の視聴状況のアニメを取得
     */
    @Query("SELECT * FROM anime_status WHERE status = :status ORDER BY updatedAt DESC")
    fun getStatusByWatchStatus(status: WatchStatus): Flow<List<AnimeStatus>>

    /**
     * 視聴状況を挿入または更新
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStatus(status: AnimeStatus)

    /**
     * 視聴状況を更新
     */
    @Update
    suspend fun updateStatus(status: AnimeStatus)

    /**
     * 視聴状況を削除
     */
    @Delete
    suspend fun deleteStatus(status: AnimeStatus)

    /**
     * 特定アニメの視聴状況を削除
     */
    @Query("DELETE FROM anime_status WHERE animeId = :animeId")
    suspend fun deleteStatusByAnimeId(animeId: Long)

    /**
     * 視聴中のアニメ数を取得
     */
    @Query("SELECT COUNT(*) FROM anime_status WHERE status = 'WATCHING'")
    fun getWatchingCount(): Flow<Int>

    /**
     * 完了したアニメ数を取得
     */
    @Query("SELECT COUNT(*) FROM anime_status WHERE status = 'COMPLETED'")
    fun getCompletedCount(): Flow<Int>
}
