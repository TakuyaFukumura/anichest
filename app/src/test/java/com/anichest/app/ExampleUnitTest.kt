package com.anichest.app

import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.WatchStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ローカルユニットテストクラス
 *
 * 開発用マシン（JVM）上で実行される単体テストです。
 * Androidデバイスやエミュレーターを必要とせず、高速に実行できます。
 *
 * テスト対象:
 * - 基本的な計算処理
 * - Animeエンティティクラスの機能
 * - ビジネスロジックの検証
 *
 * ユニットテストの利点:
 * - 高速な実行
 * - CI/CDパイプラインでの自動実行
 * - 個別クラスの詳細な検証
 * - リファクタリング時の安全性確保
 *
 * テストガイドライン:
 * - 各テストは独立して実行可能
 * - テスト名は処理内容を明確に表現
 * - Assert系メソッドで期待値を検証
 *
 * 参考リンク: [Androidテストドキュメント](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    /**
     * 基本的な算術演算のテスト
     *
     * Javaの基本的な演算が正しく動作することを確認します。
     * これはテストフレームワークの動作確認も兼ねています。
     *
     * 検証内容: 2 + 2 = 4 であることを確認
     */
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    /**
     * Animeエンティティクラスのコンストラクタテスト
     *
     * Animeオブジェクトが正しく作成され、
     * プロパティに適切な値が設定されることを確認します。
     *
     * 検証内容:
     * - IDが正しく設定される
     * - タイトルが正しく設定される
     * - エピソード数が正しく設定される
     * - オブジェクトの整合性が保たれる
     */
    @Test
    fun anime_creation_isCorrect() {
        // テスト用のAnimeを作成
        val anime = Anime(
            id = 1,
            title = "鬼滅の刃",
            totalEpisodes = 24,
            genre = "アクション",
            year = 2019,
            description = "大正時代を舞台とした剣戟奇譚"
        )

        // 設定した値が正しく取得できることを確認
        assertEquals(1, anime.id)
        assertEquals("鬼滅の刃", anime.title)
        assertEquals(24, anime.totalEpisodes)
        assertEquals("アクション", anime.genre)
        assertEquals(2019, anime.year)
    }

    /**
     * WatchStatusのenumテスト
     *
     * WatchStatusの列挙値が正しく定義されていることを確認します。
     * これはデータベースでの視聴状況管理に重要な動作です。
     *
     * 検証内容:
     * - UNWATCHED状態が存在する
     * - WATCHING状態が存在する
     * - COMPLETED状態が存在する
     * - DROPPED状態が存在する
     */
    @Test
    fun watchStatus_enumValues_areCorrect() {
        // 全ての視聴状況が定義されていることを確認
        val statuses = WatchStatus.entries.toTypedArray()
        assertEquals(4, statuses.size)

        // 各状況が正しく定義されていることを確認
        assertTrue(statuses.contains(WatchStatus.UNWATCHED))
        assertTrue(statuses.contains(WatchStatus.WATCHING))
        assertTrue(statuses.contains(WatchStatus.COMPLETED))
        assertTrue(statuses.contains(WatchStatus.DROPPED))
    }
}
