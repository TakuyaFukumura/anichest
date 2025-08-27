package com.anichest.app

import org.junit.Test
import org.junit.Assert.*
import com.example.myapplication.data.entity.StringEntity

/**
 * ローカルユニットテストクラス
 * 
 * 開発用マシン（JVM）上で実行される単体テストです。
 * Androidデバイスやエミュレーターを必要とせず、高速に実行できます。
 * 
 * テスト対象:
 * - 基本的な計算処理
 * - StringEntityクラスの機能
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
     * StringEntityクラスのコンストラクタテスト
     * 
     * StringEntityオブジェクトが正しく作成され、
     * プロパティに適切な値が設定されることを確認します。
     * 
     * 検証内容:
     * - IDが正しく設定される
     * - 文字列値が正しく設定される
     * - オブジェクトの整合性が保たれる
     */
    @Test
    fun stringEntity_creation_isCorrect() {
        // テスト用のStringEntityを作成
        val entity = StringEntity(id = 1, value = "Android")
        
        // 設定した値が正しく取得できることを確認
        assertEquals(1, entity.id)
        assertEquals("Android", entity.value)
    }
    
    /**
     * StringEntityのデフォルトIDテスト
     * 
     * StringEntityのIDにデフォルト値（0）が正しく設定されることを確認します。
     * これはRoom データベースでの自動ID生成時に重要な動作です。
     * 
     * 検証内容:
     * - IDが指定されない場合にデフォルト値0が設定される
     * - 文字列値は正しく設定される
     * - データベース挿入時の動作をシミュレート
     */
    @Test
    fun stringEntity_defaultId_isZero() {
        // IDを指定せずにStringEntityを作成（デフォルト値使用）
        val entity = StringEntity(value = "Test")
        
        // デフォルトIDが0に設定されることを確認
        assertEquals(0, entity.id)
        assertEquals("Test", entity.value)
    }
}
