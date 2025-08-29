package com.anichest.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * インストルメンテーションテストクラス
 *
 * Androidデバイスまたはエミュレーター上で実行される統合テストです。
 * 実際のAndroid環境でのアプリケーションの動作を検証します。
 *
 * テスト対象:
 * - アプリケーションコンテキストの取得
 * - MyApplicationクラスの正しい動作
 * - Androidフレームワークとの統合
 *
 * インストルメンテーションテストの特徴:
 * - 実際のAndroid環境で実行
 * - アプリの実際の動作を検証
 * - UIテストやサービステストが可能
 * - ユニットテストより実行時間が長い
 *
 * 実行環境:
 * - 物理デバイス上での実行
 * - エミュレーター上での実行
 * - CI/CDでの自動テスト実行
 *
 * テストランナー:
 * @RunWith(AndroidJUnit4::class) により、Android対応の
 * JUnit4テストランナーを使用します。
 *
 * 参考リンク: [Androidテストドキュメント](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    /**
     * アプリケーションコンテキストの取得と検証テスト
     *
     * Androidアプリが正しいパッケージ名で動作していることを確認します。
     * これはアプリケーションの基本的な設定が正しいことを検証する重要なテストです。
     *
     * 検証内容:
     * - アプリケーションコンテキストが正しく取得できる
     * - パッケージ名が期待値と一致する
     * - Androidシステムとの統合が正常
     *
     * InstrumentationRegistry:
     * - テスト実行時のAndroid環境にアクセスする手段
     * - アプリケーションコンテキストやリソースの取得
     * - テストとアプリの橋渡し役
     */
    @Test
    fun useAppContext() {
        // テスト対象アプリのコンテキストを取得
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // パッケージ名が正しく設定されていることを確認
        assertEquals("com.anichest.app", appContext.packageName)
    }

    /**
     * MyApplicationクラスの型検証テスト
     *
     * アプリケーションクラスが正しくMyApplicationとして
     * インスタンス化されていることを確認します。
     *
     * 検証内容:
     * - ApplicationクラスがMyApplicationとして認識される
     * - AndroidManifest.xmlの設定が正しく反映されている
     * - データベースとリポジトリの初期化準備が整っている
     *
     * 重要性:
     * - MVVMアーキテクチャの基盤となるクラス
     * - データベース接続の責任を持つ
     * - アプリ全体のライフサイクル管理
     *
     * テスト手法:
     * - instanceof演算子による型チェック
     * - assertTrue での真偽値検証
     * - わかりやすいエラーメッセージの提供
     */
    @Test
    fun myApplication_isCorrectType() {
        // アプリケーションコンテキストを取得
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val application = appContext.applicationContext

        // アプリケーションクラスがMyApplicationのインスタンスであることを確認
        assertTrue(
            "Application should be instance of MyApplication",
            application is MyApplication
        )
    }
}
