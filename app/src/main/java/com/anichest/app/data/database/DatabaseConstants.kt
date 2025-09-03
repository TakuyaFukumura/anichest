package com.anichest.app.data.database

/**
 * データベース関連の定数を管理するオブジェクト
 *
 * データベース名、バージョン、サンプルデータなどの
 * 固定値を一元管理することで保守性を向上させる。
 */
object DatabaseConstants {
    
    /**
     * データベース名
     */
    const val DATABASE_NAME = "anichest_database"
    
    /**
     * サンプルデータの作品数
     */
    const val SAMPLE_ANIME_COUNT = 5
    
    /**
     * デフォルトの年（不明な場合）
     */
    const val DEFAULT_YEAR = 0
    
    /**
     * デフォルトの話数（不明な場合）
     */
    const val DEFAULT_EPISODES = 0
}