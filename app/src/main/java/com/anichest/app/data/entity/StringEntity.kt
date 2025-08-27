package com.anichest.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * データベースに格納する文字列を表すエンティティクラス
 *
 * Roomデータベースライブラリを使用して、SQLiteデータベースの
 * テーブル構造を定義するデータクラスです。
 *
 * テーブル仕様:
 * - テーブル名: "strings"
 * - 主キー: id（自動生成）
 * - データカラム: value（文字列）
 *
 * 使用例:
 * - 「Android」という文字列を保存
 * - アプリで表示するメッセージの管理
 * - 将来的には設定値や多言語対応文字列の保存にも拡張可能
 *
 * Roomの特徴:
 * - SQLiteの型安全なアクセスを提供
 * - コンパイル時にSQLクエリの検証
 * - Kotlinのデータクラスと完全統合
 *
 * @property id 主キー。自動生成される一意の識別子。
 *              新しいレコード作成時はデフォルト値0を使用し、
 *              データベースが自動で適切な値を割り当てます。
 *
 * @property value 保存する文字列データ。
 *                 現在は「Android」が格納されていますが、
 *                 将来的には任意の文字列を保存可能です。
 */
@Entity(tableName = "strings")
data class StringEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String
)
