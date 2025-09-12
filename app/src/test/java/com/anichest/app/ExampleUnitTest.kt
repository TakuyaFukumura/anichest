package com.anichest.app

import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.navigation.NavigationDestination
import com.anichest.app.ui.util.WatchStatusUtils
import com.anichest.app.ui.viewmodel.AddWishlistUiState
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
            id = 1L,
            title = "鬼滅の刃",
            totalEpisodes = 24,
            genre = "アクション",
            year = 2019,
            description = "大正時代を舞台とした剣戟奇譚"
        )

        // 設定した値が正しく取得できることを確認
        assertEquals(1L, anime.id)
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

    /**
     * AddWishlistUiStateのデフォルト視聴ステータステスト
     *
     * ウィッシュリスト追加時にデフォルトで未視聴状態が設定されることを確認します。
     * これは新規アニメ登録時の視聴ステータス機能の基本動作です。
     *
     * 検証内容:
     * - デフォルトの視聴ステータスがUNWATCHEDであること
     */
    @Test
    fun addWishlistUiState_defaultWatchStatus_isUnwatched() {
        val uiState = AddWishlistUiState()
        assertEquals(WatchStatus.UNWATCHED, uiState.watchStatus)
    }

    /**
     * NavigationDestination.AnimeListのフィルター機能テスト
     *
     * アニメリスト画面へのナビゲーション時にフィルターパラメータが
     * 正しく設定できることを確認します。
     *
     * 検証内容:
     * - フィルターなしの場合はnullが設定される
     * - WATCHING状態でのフィルターが正しく設定される
     * - COMPLETED状態でのフィルターが正しく設定される
     */
    @Test
    fun navigationDestination_animeListFilter_isCorrect() {
        // フィルターなしの場合
        val destinationWithoutFilter = NavigationDestination.AnimeList()
        assertEquals(null, destinationWithoutFilter.filter)

        // 視聴中フィルターの場合
        val destinationWithWatchingFilter = NavigationDestination.AnimeList(WatchStatus.WATCHING)
        assertEquals(WatchStatus.WATCHING, destinationWithWatchingFilter.filter)

        // 視聴済フィルターの場合
        val destinationWithCompletedFilter = NavigationDestination.AnimeList(WatchStatus.COMPLETED)
        assertEquals(WatchStatus.COMPLETED, destinationWithCompletedFilter.filter)
    }

    /**
     * AnimeStatusのデフォルト値テスト
     *
     * AnimeStatusエンティティが適切なデフォルト値を持つことを確認します。
     * これは視聴ステータス機能の基盤となる重要な動作です。
     *
     * 検証内容:
     * - デフォルトの視聴ステータスがUNWATCHEDであること
     * - デフォルトの評価が0（未評価）であること
     * - デフォルトの視聴済み話数が0であること
     */
    @Test
    fun animeStatus_defaultValues_areCorrect() {
        val animeStatus = AnimeStatus(animeId = 1L)
        
        assertEquals(WatchStatus.UNWATCHED, animeStatus.status)
        assertEquals(0, animeStatus.rating)
        assertEquals("", animeStatus.review)
        assertEquals(0, animeStatus.watchedEpisodes)
        assertEquals("", animeStatus.startDate)
        assertEquals("", animeStatus.finishDate)
    }

    /**
     * WatchStatusUtilsの日本語テキスト変換テスト
     *
     * 視聴ステータスが適切な日本語テキストに変換されることを確認します。
     * これは編集画面のドロップダウンで表示される文字列の正確性を保証します。
     *
     * 検証内容:
     * - UNWATCHED → "未視聴"
     * - WATCHING → "視聴中"  
     * - COMPLETED → "視聴済"
     * - DROPPED → "中止"
     */
    @Test
    fun watchStatusUtils_getWatchStatusText_isCorrect() {
        assertEquals("未視聴", WatchStatusUtils.getWatchStatusText(WatchStatus.UNWATCHED))
        assertEquals("視聴中", WatchStatusUtils.getWatchStatusText(WatchStatus.WATCHING))
        assertEquals("視聴済", WatchStatusUtils.getWatchStatusText(WatchStatus.COMPLETED))
        assertEquals("中止", WatchStatusUtils.getWatchStatusText(WatchStatus.DROPPED))
    }
}
