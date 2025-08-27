package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.entity.StringEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文字列データにアクセスするためのDAO（Data Access Object）
 *
 * Roomデータベースライブラリを使用して、StringEntityテーブルへの
 * データアクセス操作を定義するインターフェースです。
 *
 * DAOパターンの利点:
 * - データベースアクセス操作の抽象化
 * - SQLクエリのコンパイル時検証
 * - 型安全なデータベース操作
 * - ユニットテストでのモック作成が容易
 *
 * 実装される操作:
 * - データの読み取り（Query）
 * - データの挿入（Insert）
 * - データの削除（Delete）
 *
 * 非同期処理:
 * - Flow: リアクティブなデータ監視
 * - suspend: コルーチンによる非同期実行
 */
@Dao
interface StringDao {

    /**
     * すべての文字列エンティティを取得する
     *
     * データベースの "strings" テーブルからすべてのレコードを取得します。
     * Flowを使用することで、データの変更を自動的に監視し、
     * UIに対してリアクティブな更新を提供します。
     *
     * 特徴:
     * - データが変更されると自動的に新しい値が通知される
     * - UIスレッドをブロックしない非同期処理
     * - リアルタイムでのデータ同期
     *
     * @return すべてのStringEntityのリストを含むFlow
     *         データが空の場合は空のリストが返される
     */
    @Query("SELECT * FROM strings")
    fun getAllStrings(): Flow<List<StringEntity>>

    /**
     * 最初の文字列エンティティを取得する（Android文字列用）
     *
     * データベースから1件のレコードのみを取得します。
     * 現在のアプリでは「Android」文字列を表示するために使用されています。
     *
     * LIMIT句を使用することで:
     * - データベースアクセスの効率化
     * - 必要最小限のデータ取得
     * - パフォーマンスの向上
     *
     * @return 最初のStringEntity、データが存在しない場合はnull
     */
    @Query("SELECT * FROM strings LIMIT 1")
    suspend fun getFirstString(): StringEntity?

    /**
     * 文字列エンティティを挿入する
     *
     * 新しいStringEntityをデータベースに追加します。
     * 主キー（id）は自動生成されるため、呼び出し側では
     * idを指定する必要はありません。
     *
     * トランザクション:
     * - Roomが自動的にトランザクションを管理
     * - 失敗時は自動的にロールバック
     * - データの整合性を保証
     *
     * @param string 挿入するStringEntityオブジェクト
     *               通常は StringEntity(value = "文字列") の形式で作成
     */
    @Insert
    suspend fun insertString(string: StringEntity)

    /**
     * すべての文字列エンティティを削除する
     *
     * データベースの "strings" テーブルからすべてのレコードを削除します。
     * 主にテスト時やデータのリセット時に使用されます。
     *
     * 用途例:
     * - アプリの初期化処理
     * - テストデータのクリーンアップ
     * - データベースのリセット機能
     *
     * 注意: この操作は元に戻すことができません
     */
    @Query("DELETE FROM strings")
    suspend fun deleteAllStrings()
}
