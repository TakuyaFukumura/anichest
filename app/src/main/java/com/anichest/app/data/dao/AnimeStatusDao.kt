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
 * 
 * アニメの視聴進行状況、評価、レビューなどの管理を行います。
 * 統計機能や視聴状況による検索機能も提供します。
 * 
 * @see AnimeStatus
 * @see WatchStatus
 */
@Dao
interface AnimeStatusDao {

    /**
     * 全ての視聴状況を取得
     * 更新日時の降順でソートされます
     * 
     * @return 全ての視聴状況のFlow
     */
    @Query("SELECT * FROM anime_status ORDER BY updatedAt DESC")
    fun getAllStatus(): Flow<List<AnimeStatus>>

    /**
     * 特定アニメの視聴状況を取得
     * 
     * @param animeId 対象のアニメID
     * @return 視聴状況、存在しない場合はnull
     */
    @Query("SELECT * FROM anime_status WHERE animeId = :animeId")
    suspend fun getStatusByAnimeId(animeId: Long): AnimeStatus?

    /**
     * 特定の視聴状況のアニメを取得
     * 更新日時の降順でソートされます
     * 
     * @param status 取得対象の視聴状況
     * @return 指定された視聴状況のアニメリストのFlow
     */
    @Query("SELECT * FROM anime_status WHERE status = :status ORDER BY updatedAt DESC")
    fun getStatusByWatchStatus(status: WatchStatus): Flow<List<AnimeStatus>>

    /**
     * 視聴状況を挿入または更新
     * 既存のレコードがある場合は置き換えます
     * 
     * @param status 挿入または更新する視聴状況
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStatus(status: AnimeStatus)

    /**
     * 視聴状況を更新
     * 既存のレコードが存在することが前提です
     * 
     * @param status 更新する視聴状況
     */
    @Update
    suspend fun updateStatus(status: AnimeStatus)

    /**
     * 視聴状況を削除
     * 
     * @param status 削除する視聴状況
     */
    @Delete
    suspend fun deleteStatus(status: AnimeStatus)

    /**
     * 特定アニメの視聴状況を削除
     * 
     * @param animeId 削除対象のアニメID
     */
    @Query("DELETE FROM anime_status WHERE animeId = :animeId")
    suspend fun deleteStatusByAnimeId(animeId: Long)

    /**
     * 視聴中のアニメ数を取得
     * ホーム画面の統計表示に使用されます
     * 
     * @return 視聴中のアニメ数のFlow
     */
    @Query("SELECT COUNT(*) FROM anime_status WHERE status = 'WATCHING'")
    fun getWatchingCount(): Flow<Int>

    /**
     * 完了したアニメ数を取得
     * ホーム画面の統計表示に使用されます
     * 
     * @return 視聴完了のアニメ数のFlow
     */
    @Query("SELECT COUNT(*) FROM anime_status WHERE status = 'COMPLETED'")
    fun getCompletedCount(): Flow<Int>
}
