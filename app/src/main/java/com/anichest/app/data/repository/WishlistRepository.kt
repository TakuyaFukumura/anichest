package com.anichest.app.data.repository

import com.anichest.app.data.dao.WishlistDao
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.AnimeWithWishlistAndStatus
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * ウィッシュリストデータアクセスを抽象化するRepository
 */
class WishlistRepository(private val wishlistDao: WishlistDao) {

    fun getAllWishlistItems(): Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()

    fun getWishlistWithAnime(): Flow<List<AnimeWithWishlist>> = wishlistDao.getWishlistWithAnime()

    fun getUnwatchedWishlistWithAnime(): Flow<List<AnimeWithWishlistAndStatus>> = 
        wishlistDao.getUnwatchedWishlistWithAnime()

    fun getWishlistByPriority(priority: Priority): Flow<List<WishlistItem>> =
        wishlistDao.getWishlistByPriority(priority)

    suspend fun getWishlistItemByAnimeId(animeId: Long): WishlistItem? =
        wishlistDao.getWishlistItemByAnimeId(animeId)

    suspend fun insertWishlistItem(item: WishlistItem) = wishlistDao.insertWishlistItem(item)

    suspend fun updateWishlistItem(item: WishlistItem) = wishlistDao.updateWishlistItem(item)

    suspend fun deleteWishlistItem(item: WishlistItem) = wishlistDao.deleteWishlistItem(item)

    suspend fun deleteWishlistItemByAnimeId(animeId: Long) =
        wishlistDao.deleteWishlistItemByAnimeId(animeId)

    fun getWishlistCount(): Flow<Int> = wishlistDao.getWishlistCount()
}
