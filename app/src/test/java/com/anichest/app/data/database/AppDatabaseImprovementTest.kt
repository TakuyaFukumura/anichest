package com.anichest.app.data.database

import com.anichest.app.data.entity.Anime
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * AppDatabaseの改善点をテストするクラス
 *
 * 主に以下の機能をテストする：
 * - 架空アニメタイトルの使用確認
 * - データベース定数の整合性確認
 */
class AppDatabaseImprovementTest {

    @Test
    fun `sample anime count matches expected count`() {
        // サンプルアニメ数が期待値と一致することを確認
        assertEquals(5, DatabaseConstants.SAMPLE_ANIME_COUNT)
    }

    @Test
    fun `database constants are properly defined`() {
        // データベース名が正しく定義されていることを確認
        assertEquals("anichest_database", DatabaseConstants.DATABASE_NAME)

        // デフォルト値が適切に定義されていることを確認
        assertEquals(2020, DatabaseConstants.DEFAULT_YEAR)
        assertEquals(12, DatabaseConstants.DEFAULT_EPISODES)
    }

    @Test
    fun `anime entity uses proper default values`() {
        // デフォルト値でアニメエンティティを作成
        val anime = Anime(
            title = "テストアニメ",
            totalEpisodes = DatabaseConstants.DEFAULT_EPISODES,
            year = DatabaseConstants.DEFAULT_YEAR
        )

        assertEquals(12, anime.totalEpisodes)
        assertEquals(2020, anime.year)
        assertEquals("", anime.genre)
        assertEquals("", anime.description)
    }
}
