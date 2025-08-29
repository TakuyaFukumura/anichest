package com.anichest.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * ウィッシュリストデータへのアクセスを提供するDAO
 */
@Dao
interface WishlistDao {

    /**
     * 全てのウィッシュリストアイテムを取得
     */
    @Query("SELECT * FROM wishlist ORDER BY priority DESC, addedAt DESC")
    fun getAllWishlistItems(): Flow<List<WishlistItem>>

    /**
     * アニメとウィッシュリスト情報を結合して取得
     */
    @Transaction
    @Query(
        """
        SELECT anime.* FROM anime 
        INNER JOIN wishlist ON anime.id = wishlist.animeId 
        ORDER BY wishlist.priority DESC, wishlist.addedAt DESC
    """
    )
    fun getWishlistWithAnime(): Flow<List<AnimeWithWishlist>>

    /**
     * 特定の優先度のウィッシュリストを取得
     */
    @Query("SELECT * FROM wishlist WHERE priority = :priority ORDER BY addedAt DESC")
    fun getWishlistByPriority(priority: Priority): Flow<List<WishlistItem>>

    /**
     * 特定アニメのウィッシュリストアイテムを取得
     */
    @Query("SELECT * FROM wishlist WHERE animeId = :animeId")
    suspend fun getWishlistItemByAnimeId(animeId: Int): WishlistItem?

    /**
     * ウィッシュリストアイテムを挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItem)

    /**
     * ウィッシュリストアイテムを更新
     */
    @Update
    suspend fun updateWishlistItem(item: WishlistItem)

    /**
     * ウィッシュリストアイテムを削除
     */
    @Delete
    suspend fun deleteWishlistItem(item: WishlistItem)

    /**
     * 特定アニメのウィッシュリストアイテムを削除
     */
    @Query("DELETE FROM wishlist WHERE animeId = :animeId")
    suspend fun deleteWishlistItemByAnimeId(animeId: Int)

    /**
     * ウィッシュリストの総数を取得
     */
    @Query("SELECT COUNT(*) FROM wishlist")
    fun getWishlistCount(): Flow<Int>
}