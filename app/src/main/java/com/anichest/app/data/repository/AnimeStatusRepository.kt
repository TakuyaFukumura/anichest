package com.anichest.app.data.repository

import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * アニメ視聴状況データアクセスを抽象化するRepository
 * 
 * このクラスはアニメの視聴状況（視聴中、完了、評価など）に関するデータ操作を提供し、
 * DAOとビジネスロジック層の間の抽象化レイヤーとして機能します。
 * 
 * @param animeStatusDao アニメ視聴状況データアクセスオブジェクト
 * @see AnimeStatusDao
 * @see AnimeStatus
 * @see WatchStatus
 */
class AnimeStatusRepository(private val animeStatusDao: AnimeStatusDao) {

    /**
     * 全ての視聴状況を取得
     * 
     * @return 全ての視聴状況のFlow
     */
    fun getAllStatus(): Flow<List<AnimeStatus>> = animeStatusDao.getAllStatus()

    /**
     * 指定された視聴ステータスの視聴状況を取得
     * 
     * @param status 取得する視聴ステータス
     * @return 指定されたステータスの視聴状況のFlow
     */
    fun getStatusByWatchStatus(status: WatchStatus): Flow<List<AnimeStatus>> =
        animeStatusDao.getStatusByWatchStatus(status)

    /**
     * アニメIDで視聴状況を取得
     * 
     * @param animeId アニメ作品のID
     * @return 対象の視聴状況、存在しない場合はnull
     */
    suspend fun getStatusByAnimeId(animeId: Long): AnimeStatus? =
        animeStatusDao.getStatusByAnimeId(animeId)

    /**
     * 視聴状況を挿入または更新
     * 既存のレコードがある場合は更新、ない場合は新規挿入を行います。
     * 
     * @param status 挿入または更新する視聴状況
     */
    suspend fun insertOrUpdateStatus(status: AnimeStatus) =
        animeStatusDao.insertOrUpdateStatus(status)

    /**
     * 視聴状況を更新
     * 
     * @param status 更新する視聴状況
     */
    suspend fun updateStatus(status: AnimeStatus) = animeStatusDao.updateStatus(status)

    /**
     * 視聴状況を削除
     * 
     * @param status 削除する視聴状況
     */
    suspend fun deleteStatus(status: AnimeStatus) = animeStatusDao.deleteStatus(status)

    /**
     * アニメIDで視聴状況を削除
     * 
     * @param animeId 削除対象のアニメ作品ID
     */
    suspend fun deleteStatusByAnimeId(animeId: Long) = animeStatusDao.deleteStatusByAnimeId(animeId)

    /**
     * 視聴中のアニメ数を取得
     * 
     * @return 視聴中のアニメ数のFlow
     */
    fun getWatchingCount(): Flow<Int> = animeStatusDao.getWatchingCount()

    /**
     * 視聴完了のアニメ数を取得
     * 
     * @return 視聴完了のアニメ数のFlow
     */
    fun getCompletedCount(): Flow<Int> = animeStatusDao.getCompletedCount()

    /**
     * 未視聴のアニメ数を取得
     * 
     * @return 未視聴のアニメ数のFlow
     */
    fun getUnwatchedCount(): Flow<Int> = animeStatusDao.getUnwatchedCount()

    /**
     * 中止したアニメ数を取得
     * 
     * @return 中止したアニメ数のFlow
     */
    fun getDroppedCount(): Flow<Int> = animeStatusDao.getDroppedCount()
}
