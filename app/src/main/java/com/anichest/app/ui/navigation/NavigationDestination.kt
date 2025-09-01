package com.anichest.app.ui.navigation

import com.anichest.app.data.entity.WatchStatus

/**
 * ナビゲーション先を定義するsealedクラス
 */
sealed class NavigationDestination {
    object Home : NavigationDestination()
    data class AnimeList(val filter: WatchStatus? = null) : NavigationDestination()
    object Wishlist : NavigationDestination()
    object AddWishlist : NavigationDestination()
    data class AnimeDetail(val animeId: Long) : NavigationDestination()
}
