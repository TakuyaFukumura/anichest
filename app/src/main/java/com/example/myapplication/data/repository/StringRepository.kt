package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.StringDao
import com.example.myapplication.data.entity.StringEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文字列データにアクセスするリポジトリクラス
 *
 * リポジトリパターンを実装し、ViewModelとDAO間の中間層として機能します。
 * データアクセスの詳細をViewModelから隠蔽し、ビジネスロジックの
 * 分離とテスタビリティの向上を実現します。
 *
 * リポジトリパターンの利点:
 * - データソースの抽象化（データベース、ネットワーク、キャッシュなど）
 * - ViewModelからのデータアクセス詳細の隠蔽
 * - 複数のデータソースの統合
 * - ユニットテストでのモック作成の容易さ
 * - ビジネスロジックの集約
 *
 * アーキテクチャ上の位置:
 * ViewModel → Repository → DAO → Database
 *
 * 現在の実装:
 * - StringDaoへの単純な委譲
 * - 将来的にはキャッシュ機能やネットワーク処理の追加が可能
 *
 * @property stringDao StringEntityへのデータアクセスを提供するDAO
 */
class StringRepository(private val stringDao: StringDao) {

    /**
     * すべての文字列エンティティを取得する
     *
     * データベースに保存されているすべての文字列を取得します。
     * Flowを返すことで、データの変更をリアルタイムで監視できます。
     *
     * 使用場面:
     * - 文字列一覧の表示
     * - データの変更監視
     * - リアクティブUI の更新
     *
     * パフォーマンス考慮:
     * - Flowはコールドストリームのため、collect開始時にデータ取得開始
     * - データベースの変更時のみ新しい値を emit
     * - バックグラウンドスレッドで実行され、UIスレッドをブロックしない
     *
     * @return すべてのStringEntityのリストを含むFlow
     */
    fun getAllStrings(): Flow<List<StringEntity>> = stringDao.getAllStrings()

    /**
     * 最初の文字列エンティティを取得する（「Android」文字列）
     *
     * データベースから1件のStringEntityを取得します。
     * 現在のアプリでは「Hello Android!」メッセージの表示に使用されます。
     *
     * 実装詳細:
     * - suspend関数のため、コルーチン内で呼び出す必要がある
     * - データが存在しない場合はnullを返す
     * - ViewModelで適切なエラーハンドリングが実装されている
     *
     * エラーハンドリング:
     * - データが存在しない場合: null を返す
     * - データベースエラー: 例外がスローされる（ViewModelで catch）
     *
     * @return 最初のStringEntity、存在しない場合はnull
     */
    suspend fun getFirstString(): StringEntity? = stringDao.getFirstString()

    /**
     * 文字列エンティティを挿入する
     *
     * 新しいStringEntityをデータベースに保存します。
     * アプリの初期化時やデータの追加時に使用されます。
     *
     * 使用場面:
     * - アプリ初期化時の「Android」文字列挿入
     * - 新しい文字列データの追加
     * - データベースのシード処理
     *
     * トランザクション:
     * - Roomが自動的にトランザクションを管理
     * - 失敗時は自動ロールバック
     * - データの整合性を保証
     *
     * @param string 挿入するStringEntityオブジェクト
     *               例: StringEntity(value = "Android")
     */
    suspend fun insertString(string: StringEntity) = stringDao.insertString(string)
}
