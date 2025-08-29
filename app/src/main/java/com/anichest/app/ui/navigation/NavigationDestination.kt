package com.anichest.app.ui.navigation

/**
 * ナビゲーション先を定義するsealedクラス
 */
sealed class NavigationDestination {
    object Home : NavigationDestination()
    object AnimeList : NavigationDestination()
    object Wishlist : NavigationDestination()
    data class AnimeDetail(val animeId: Int) : NavigationDestination()
}