package com.anichest.app.data.repository

import com.anichest.app.data.dao.WishlistDao
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.AnimeWithWishlistAndStatus
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * ウィッシュリストデータアクセスを抽象化するRepository
 * 
 * このクラスは視聴予定アニメのウィッシュリスト管理に関するデータ操作を提供し、
 * DAOとビジネスロジック層の間の抽象化レイヤーとして機能します。
 * 
 * @param wishlistDao ウィッシュリストデータアクセスオブジェクト
 * @see WishlistDao
 * @see WishlistItem
 * @see AnimeWithWishlist
 * @see Priority
 */
class WishlistRepository(private val wishlistDao: WishlistDao) {

    /**
     * 全てのウィッシュリストアイテムを取得
     * 
     * @return 全てのウィッシュリストアイテムのFlow
     */
    fun getAllWishlistItems(): Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()

    /**
     * アニメ情報と結合したウィッシュリストを取得
     * 
     * @return アニメ情報と結合したウィッシュリストのFlow
     */
    fun getWishlistWithAnime(): Flow<List<AnimeWithWishlist>> = wishlistDao.getWishlistWithAnime()

    /**
     * 未視聴のウィッシュリストとアニメ・視聴状況を結合して取得
     * 
     * @return 未視聴のウィッシュリストと関連情報のFlow
     */
    fun getUnwatchedWishlistWithAnime(): Flow<List<AnimeWithWishlistAndStatus>> =
        wishlistDao.getUnwatchedWishlistWithAnime()

    /**
     * 指定された優先度のウィッシュリストアイテムを取得
     * 
     * @param priority 取得する優先度
     * @return 指定された優先度のウィッシュリストアイテムのFlow
     */
    fun getWishlistByPriority(priority: Priority): Flow<List<WishlistItem>> =
        wishlistDao.getWishlistByPriority(priority)

    /**
     * アニメIDでウィッシュリストアイテムを取得
     * 
     * @param animeId アニメ作品のID
     * @return 対象のウィッシュリストアイテム、存在しない場合はnull
     */
    suspend fun getWishlistItemByAnimeId(animeId: Long): WishlistItem? =
        wishlistDao.getWishlistItemByAnimeId(animeId)

    /**
     * ウィッシュリストアイテムを挿入
     * 
     * @param item 挿入するウィッシュリストアイテム
     */
    suspend fun insertWishlistItem(item: WishlistItem) = wishlistDao.insertWishlistItem(item)

    /**
     * ウィッシュリストアイテムを更新
     * 
     * @param item 更新するウィッシュリストアイテム
     */
    suspend fun updateWishlistItem(item: WishlistItem) = wishlistDao.updateWishlistItem(item)

    /**
     * ウィッシュリストアイテムを削除
     * 
     * @param item 削除するウィッシュリストアイテム
     */
    suspend fun deleteWishlistItem(item: WishlistItem) = wishlistDao.deleteWishlistItem(item)

    /**
     * アニメIDでウィッシュリストアイテムを削除
     * 
     * @param animeId 削除対象のアニメ作品ID
     */
    suspend fun deleteWishlistItemByAnimeId(animeId: Long) =
        wishlistDao.deleteWishlistItemByAnimeId(animeId)

    /**
     * ウィッシュリストアイテムの総数を取得
     * 
     * @return ウィッシュリストアイテム数のFlow
     */
    fun getWishlistCount(): Flow<Int> = wishlistDao.getWishlistCount()
}
