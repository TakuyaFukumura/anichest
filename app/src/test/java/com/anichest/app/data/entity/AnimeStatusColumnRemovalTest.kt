package com.anichest.app.data.entity

import org.junit.Test
import org.junit.Assert.*

/**
 * AnimeStatusからの不要カラム削除を検証するテスト
 * 
 * このテストは以下を確認します：
 * - AnimeStatusクラスに削除対象のフィールドが存在しないこと
 * - 基本的なプロパティが正常に動作すること
 */
class AnimeStatusColumnRemovalTest {

    @Test
    fun `AnimeStatus should not have removed fields`() {
        // AnimeStatusインスタンスを作成
        val animeStatus = AnimeStatus(
            animeId = 123L,
            status = WatchStatus.WATCHING,
            rating = 4,
            review = "面白いアニメです",
            watchedEpisodes = 5
        )

        // 基本プロパティが正常に設定されることを確認
        assertEquals(123L, animeStatus.animeId)
        assertEquals(WatchStatus.WATCHING, animeStatus.status)
        assertEquals(4, animeStatus.rating)
        assertEquals("面白いアニメです", animeStatus.review)
        assertEquals(5, animeStatus.watchedEpisodes)

        // リフレクションを使用して削除されたフィールドが存在しないことを確認
        val fields = AnimeStatus::class.java.declaredFields
        val fieldNames = fields.map { it.name }

        // 削除されたフィールドが存在しないことを確認
        assertFalse("startDate field should not exist", fieldNames.contains("startDate"))
        assertFalse("finishDate field should not exist", fieldNames.contains("finishDate"))
        assertFalse("updatedAt field should not exist", fieldNames.contains("updatedAt"))
        
        // 残すべきフィールドが存在することを確認
        assertTrue("id field should exist", fieldNames.contains("id"))
        assertTrue("animeId field should exist", fieldNames.contains("animeId"))
        assertTrue("status field should exist", fieldNames.contains("status"))
        assertTrue("rating field should exist", fieldNames.contains("rating"))
        assertTrue("review field should exist", fieldNames.contains("review"))
        assertTrue("watchedEpisodes field should exist", fieldNames.contains("watchedEpisodes"))
    }

    @Test
    fun `AnimeStatus default constructor should work without removed fields`() {
        // デフォルト値でのインスタンス作成が成功することを確認
        val animeStatus = AnimeStatus(animeId = 1L)
        
        assertEquals(0, animeStatus.id)
        assertEquals(1L, animeStatus.animeId)
        assertEquals(WatchStatus.UNWATCHED, animeStatus.status)
        assertEquals(0, animeStatus.rating)
        assertEquals("", animeStatus.review)
        assertEquals(0, animeStatus.watchedEpisodes)
    }
}