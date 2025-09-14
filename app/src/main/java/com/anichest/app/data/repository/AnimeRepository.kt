package com.anichest.app.data.repository

import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.AnimeWithStatus
import kotlinx.coroutines.flow.Flow

/**
 * アニメデータアクセスを抽象化するRepository
 *
 * このクラスはアニメ作品に関するデータ操作を提供し、
 * DAOとビジネスロジック層の間の抽象化レイヤーとして機能します。
 *
 * @param animeDao アニメデータアクセスオブジェクト
 * @see AnimeDao
 * @see Anime
 * @see AnimeWithStatus
 */
class AnimeRepository(private val animeDao: AnimeDao) {

    /**
     * 全てのアニメ作品を取得
     *
     * @return タイトル順でソートされたアニメ作品のFlow
     */
    fun getAllAnime(): Flow<List<Anime>> = animeDao.getAllAnime()

    /**
     * 全てのアニメ作品と視聴状況を結合して取得
     *
     * @return アニメ作品と視聴状況の結合データのFlow
     */
    fun getAllAnimeWithStatus(): Flow<List<AnimeWithStatus>> = animeDao.getAllAnimeWithStatus()

    /**
     * タイトルでアニメ作品を検索
     *
     * @param query 検索クエリ（部分一致）
     * @return 検索条件にマッチするアニメ作品のFlow
     */
    fun searchAnimeByTitle(query: String): Flow<List<Anime>> = animeDao.searchAnimeByTitle(query)

    /**
     * 特定の視聴状況のアニメを取得
     *
     * @param status 視聴状況（"WATCHING", "COMPLETED", "UNWATCHED" など）
     * @return 指定された視聴状況のアニメ作品のFlow
     */
    fun getAnimeByStatus(status: String): Flow<List<AnimeWithStatus>> =
        animeDao.getAnimeByStatus(status)

    /**
     * IDでアニメ作品を取得
     *
     * @param id アニメ作品のID
     * @return 対象のアニメ作品、存在しない場合はnull
     */
    suspend fun getAnimeById(id: Long): Anime? = animeDao.getAnimeById(id)

    /**
     * アニメ作品を挿入
     *
     * @param anime 挿入するアニメ作品
     * @return 挿入されたレコードのID
     */
    suspend fun insertAnime(anime: Anime): Long = animeDao.insertAnime(anime)

    /**
     * アニメ作品を更新
     *
     * @param anime 更新するアニメ作品
     */
    suspend fun updateAnime(anime: Anime) = animeDao.updateAnime(anime)

    /**
     * アニメ作品を削除
     *
     * @param anime 削除するアニメ作品
     */
    suspend fun deleteAnime(anime: Anime) = animeDao.deleteAnime(anime)

    /**
     * IDでアニメ作品を削除
     *
     * @param id 削除するアニメ作品のID
     */
    suspend fun deleteAnimeById(id: Long) = animeDao.deleteAnimeById(id)

    /**
     * アニメ基本情報と視聴状況を原子的に更新
     *
     * 両方の更新が同一トランザクション内で実行され、
     * データの一貫性が保証されます。
     *
     * @param anime 更新するアニメ基本情報
     * @param animeStatus 更新または挿入する視聴状況
     */
    suspend fun updateAnimeAndStatus(anime: Anime, animeStatus: AnimeStatus) =
        animeDao.updateAnimeAndStatus(anime, animeStatus)
}
