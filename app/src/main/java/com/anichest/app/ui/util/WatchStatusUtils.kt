package com.anichest.app.ui.util

import com.anichest.app.data.entity.WatchStatus

/**
 * 視聴ステータス関連のユーティリティ関数
 */
object WatchStatusUtils {
    
    /**
     * WatchStatusを日本語テキストに変換する
     * @param status 視聴ステータス
     * @return 日本語のステータステキスト
     */
    fun getWatchStatusText(status: WatchStatus): String {
        return when (status) {
            WatchStatus.UNWATCHED -> "未視聴"
            WatchStatus.WATCHING -> "視聴中"
            WatchStatus.COMPLETED -> "視聴済"
            WatchStatus.DROPPED -> "中止"
        }
    }
}
