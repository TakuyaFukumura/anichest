package com.anichest.app.ui.navigation

import com.anichest.app.data.entity.WatchStatus

/**
 * ナビゲーション先を定義するsealedクラス
 * 
 * アプリ内の各画面への遷移を型安全に管理します。
 * 各画面で必要なパラメータも含めて定義されています。
 * 
 * @see WatchStatus
 */
sealed class NavigationDestination {
    
    /**
     * ホーム画面
     * アプリの最初に表示される画面で、統計情報や最近の活動を表示
     */
    object Home : NavigationDestination()
    
    /**
     * アニメリスト画面
     * 
     * @property filter 視聴状況によるフィルター（nullの場合は全て表示）
     */
    data class AnimeList(val filter: WatchStatus? = null) : NavigationDestination()
    
    /**
     * ウィッシュリスト画面
     * 視聴予定のアニメ一覧を表示
     */
    object Wishlist : NavigationDestination()
    
    /**
     * ウィッシュリスト新規追加画面
     * 新しいアニメをウィッシュリストに追加
     */
    object AddWishlist : NavigationDestination()
    
    /**
     * アニメ詳細画面
     * 
     * @property animeId 表示するアニメのID
     */
    data class AnimeDetail(val animeId: Long) : NavigationDestination()
}
