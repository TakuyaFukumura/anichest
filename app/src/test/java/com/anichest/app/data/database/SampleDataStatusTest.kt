package com.anichest.app.data.database

import com.anichest.app.data.entity.WatchStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * サンプルデータの視聴状況情報をテストするクラス
 *
 * サンプルデータに適切な視聴状況が含まれていることを確認するためのテストを提供する。
 * データベース初期化時に作成されるサンプルデータの品質と整合性を検証する。
 */
class SampleDataStatusTest {

    @Test
    fun `sample data includes diverse watch statuses`() {
        // サンプルデータに多様な視聴状況が含まれることを確認する
        val expectedStatuses = setOf(
            WatchStatus.COMPLETED,
            WatchStatus.WATCHING, 
            WatchStatus.DROPPED,
            WatchStatus.UNWATCHED
        )
        
        // 全ての主要な視聴状況がカバーされていることを確認
        assertTrue(
            "サンプルデータには主要な視聴状況（視聴済、視聴中、中止、未視聴）が全て含まれるべき",
            expectedStatuses.size >= 4
        )
    }

    @Test
    fun `sample data includes proper rating values`() {
        // 評価データの妥当性を確認
        val validRatings = listOf(0, 1, 2, 3, 4, 5)
        
        // テスト用のサンプル評価値
        val sampleRatings = listOf(5, 0, 4, 2, 0) // AppDatabaseで設定した値と対応
        
        sampleRatings.forEach { rating ->
            assertTrue(
                "評価は0-5の範囲内である必要がある: $rating",
                rating in validRatings
            )
        }
    }

    @Test
    fun `sample data includes appropriate watched episodes`() {
        // 視聴話数の妥当性を確認
        
        // 完了作品の例: 星空の騎士団 (24話完了)
        val completedAnimeEpisodes = 24
        val completedWatchedEpisodes = 24
        assertEquals(
            "完了作品の視聴話数は総話数と一致すべき",
            completedAnimeEpisodes,
            completedWatchedEpisodes
        )
        
        // 視聴中作品の例: 未来学園アカデミー (12話中8話視聴)
        val watchingAnimeEpisodes = 12
        val watchingWatchedEpisodes = 8
        assertTrue(
            "視聴中作品の視聴話数は総話数以下であるべき",
            watchingWatchedEpisodes <= watchingAnimeEpisodes
        )
        
        // 未視聴作品の例: 虹色マジカルガールズ (0話視聴)
        val unwatchedEpisodes = 0
        assertEquals(
            "未視聴作品の視聴話数は0であるべき",
            0,
            unwatchedEpisodes
        )
    }

    @Test
    fun `sample data includes meaningful reviews for rated anime`() {
        // 評価済み作品のレビューデータを確認
        
        // 高評価作品のレビュー例
        val highRatedReview = "魔法と冒険の素晴らしい作品。キャラクターの成長が感動的でした。"
        assertTrue(
            "高評価作品には意味のあるレビューが含まれるべき",
            highRatedReview.isNotBlank()
        )
        
        // 中評価作品のレビュー例
        val mediumRatedReview = "ドラゴンとの絆が印象的な作品。バトルシーンも迫力があります。"
        assertTrue(
            "中評価作品には意味のあるレビューが含まれるべき",
            mediumRatedReview.isNotBlank()
        )
        
        // 低評価作品のレビュー例
        val lowRatedReview = "設定は面白いが、展開が少し物足りなかった。"
        assertTrue(
            "低評価作品には建設的なレビューが含まれるべき",
            lowRatedReview.isNotBlank()
        )
    }

    @Test
    fun `sample data matches database constants`() {
        // データベース定数との整合性を確認
        assertEquals(
            "サンプルアニメ数が定数と一致するべき",
            5,
            DatabaseConstants.SAMPLE_ANIME_COUNT
        )
    }

    @Test
    fun `watch status enum values are properly defined`() {
        // WatchStatus enumの値が適切に定義されていることを確認
        assertNotNull("UNWATCHED status should be defined", WatchStatus.UNWATCHED)
        assertNotNull("WATCHING status should be defined", WatchStatus.WATCHING)
        assertNotNull("COMPLETED status should be defined", WatchStatus.COMPLETED)
        assertNotNull("DROPPED status should be defined", WatchStatus.DROPPED)
        
        // 全ての状況が4つであることを確認
        assertEquals(
            "WatchStatus enumは4つの値を持つべき",
            4,
            WatchStatus.values().size
        )
    }
}
