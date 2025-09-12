package com.anichest.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.AnimeWithWishlistAndStatus
import com.anichest.app.data.entity.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * ウィッシュリストデータへのアクセスを提供するDAO
 * 
 * 視聴予定アニメの管理と、アニメ情報との結合クエリを提供します。
 * 
 * @see WishlistItem
 * @see AnimeWithWishlist
 * @see AnimeWithWishlistAndStatus
 */
@Dao
interface WishlistDao {

    /**
     * 全てのウィッシュリストアイテムを取得
     * 追加日時の降順でソートされます
     * 
     * @return 全てのウィッシュリストアイテムのFlow
     */
    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun getAllWishlistItems(): Flow<List<WishlistItem>>

    /**
     * アニメとウィッシュリスト情報を結合して取得
     * アニメの基本情報とウィッシュリスト情報を一緒に取得します
     * 追加日時の降順でソートされます
     * 
     * @return アニメ・ウィッシュリスト結合データのFlow
     */
    @Transaction
    @Query(
        """
        SELECT anime.* FROM anime 
        INNER JOIN wishlist ON anime.id = wishlist.animeId 
        ORDER BY wishlist.addedAt DESC
    """
    )
    fun getWishlistWithAnime(): Flow<List<AnimeWithWishlist>>

    /**
     * 未視聴のアニメのみのウィッシュリストを取得
     * 
     * 視聴ステータスがない場合（null）も未視聴として扱います。
     * アニメ、ウィッシュリスト、視聴状況の3つのテーブルを結合し、
     * 未視聴のアニメのみを抽出します。
     * 追加日時の降順でソートされます。
     * 
     * @return 未視聴アニメのウィッシュリスト結合データのFlow
     */
    @Transaction
    @Query(
        """
        SELECT anime.* FROM anime
        INNER JOIN wishlist ON anime.id = wishlist.animeId
        LEFT JOIN anime_status ON anime.id = anime_status.animeId
        WHERE anime_status.status IS NULL OR anime_status.status = 'UNWATCHED'
        ORDER BY wishlist.addedAt DESC
        """
    )
    fun getUnwatchedWishlistWithAnime(): Flow<List<AnimeWithWishlistAndStatus>>

    /**
     * 特定アニメのウィッシュリストアイテムを取得
     * 
     * @param animeId 対象のアニメID
     * @return ウィッシュリストアイテム、存在しない場合はnull
     */
    @Query("SELECT * FROM wishlist WHERE animeId = :animeId")
    suspend fun getWishlistItemByAnimeId(animeId: Long): WishlistItem?

    /**
     * ウィッシュリストアイテムを挿入
     * 既存のレコードがある場合は置き換えます
     * 
     * @param item 挿入するウィッシュリストアイテム
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItem)

    /**
     * ウィッシュリストアイテムを更新
     * 既存のレコードが存在することが前提です
     * 
     * @param item 更新するウィッシュリストアイテム
     */
    @Update
    suspend fun updateWishlistItem(item: WishlistItem)

    /**
     * ウィッシュリストアイテムを削除
     * 
     * @param item 削除するウィッシュリストアイテム
     */
    @Delete
    suspend fun deleteWishlistItem(item: WishlistItem)

    /**
     * 特定アニメのウィッシュリストアイテムを削除
     * 
     * @param animeId 削除対象のアニメID
     */
    @Query("DELETE FROM wishlist WHERE animeId = :animeId")
    suspend fun deleteWishlistItemByAnimeId(animeId: Long)

    /**
     * ウィッシュリストの総数を取得
     * ホーム画面やヘッダーの統計表示に使用されます
     * 
     * @return ウィッシュリストアイテム数のFlow
     */
    @Query("SELECT COUNT(*) FROM wishlist")
    fun getWishlistCount(): Flow<Int>
}
