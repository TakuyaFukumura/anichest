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
     * アニメ詳細画面
     * 
     * @property animeId 表示するアニメのID
     */
    data class AnimeDetail(val animeId: Long) : NavigationDestination()
    
    /**
     * アニメ登録画面
     * 新しいアニメ情報を登録するための画面
     */
    object AnimeRegistration : NavigationDestination()
}
