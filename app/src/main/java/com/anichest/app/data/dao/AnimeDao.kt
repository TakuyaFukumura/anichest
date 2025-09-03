package com.anichest.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeWithStatus
import kotlinx.coroutines.flow.Flow

/**
 * アニメ作品データへのアクセスを提供するDAO
 */
@Dao
interface AnimeDao {

    /**
     * 全てのアニメ作品を取得
     */
    @Query("SELECT * FROM anime ORDER BY title ASC")
    fun getAllAnime(): Flow<List<Anime>>

    /**
     * IDでアニメ作品を取得
     */
    @Query("SELECT * FROM anime WHERE id = :id")
    suspend fun getAnimeById(id: Long): Anime?

    /**
     * タイトルでアニメ作品を検索
     */
    @Query("SELECT * FROM anime WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchAnimeByTitle(query: String): Flow<List<Anime>>

    /**
     * アニメ作品と視聴状況を結合して取得
     */
    @Transaction
    @Query("SELECT * FROM anime ORDER BY title ASC")
    fun getAllAnimeWithStatus(): Flow<List<AnimeWithStatus>>

    /**
     * 特定の視聴状況のアニメを取得
     */
    @Transaction
    @Query(
        """
        SELECT anime.* FROM anime 
        LEFT JOIN anime_status ON anime.id = anime_status.animeId 
        WHERE anime_status.status = :status OR (anime_status.status IS NULL AND :status = 'UNWATCHED')
        ORDER BY anime.title ASC
    """
    )
    fun getAnimeByStatus(status: String): Flow<List<AnimeWithStatus>>

    /**
     * アニメ作品を挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: Anime): Long

    /**
     * アニメ作品を更新
     */
    @Update
    suspend fun updateAnime(anime: Anime)

    /**
     * アニメ作品を削除
     */
    @Delete
    suspend fun deleteAnime(anime: Anime)

    /**
     * IDでアニメ作品を削除
     */
    @Query("DELETE FROM anime WHERE id = :id")
    suspend fun deleteAnimeById(id: Long)

    /**
     * アニメ作品の総数を取得
     * 
     * @return データベース内のアニメ作品数
     */
    @Query("SELECT COUNT(*) FROM anime")
    suspend fun getAnimeCount(): Int

    /**
     * 指定されたタイトルのアニメ作品が存在するかチェック
     * 
     * @param title チェックするアニメタイトル
     * @return 存在する場合はtrue、しない場合はfalse
     */
    @Query("SELECT EXISTS(SELECT 1 FROM anime WHERE title = :title)")
    suspend fun existsByTitle(title: String): Boolean
}
