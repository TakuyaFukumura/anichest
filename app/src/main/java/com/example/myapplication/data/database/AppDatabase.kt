package com.example.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.dao.StringDao
import com.example.myapplication.data.entity.StringEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * アプリケーションのメインデータベースクラス
 *
 * Roomデータベースライブラリを使用してSQLiteデータベースを管理します。
 * データベースの作成、更新、アクセスを統一的に管理する中心的なクラスです。
 *
 * 主な特徴:
 * - シングルトンパターンでインスタンスを管理
 * - データベーススキーマの定義
 * - DAOへのアクセス提供
 * - データベース作成時の初期化処理
 *
 * Room データベースの利点:
 * - SQLiteの複雑さを抽象化
 * - コンパイル時のクエリ検証
 * - 型安全なデータベースアクセス
 * - 自動的なマイグレーション対応
 *
 * データベース仕様:
 * - データベース名: "app_database"
 * - バージョン: 1（初期版）
 * - エンティティ: StringEntity（文字列テーブル）
 * - エクスポートスキーマ: false（開発用設定）
 */
@Database(
    entities = [StringEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * StringDaoへのアクセスを提供する抽象メソッド
     *
     * Roomがコンパイル時に自動実装を生成します。
     * このメソッドを通じてStringEntityに対するデータベース操作を行います。
     *
     * @return StringDaoの実装インスタンス
     */
    abstract fun stringDao(): StringDao

    companion object {
        /**
         * データベースインスタンスのキャッシュ用変数
         *
         * @Volatileアノテーションにより、マルチスレッド環境での
         * 可視性を保証します。複数のスレッドから同時にアクセスされても
         * 最新の値が確実に読み取れます。
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * データベースインスタンスを取得する（シングルトンパターン）
         *
         * アプリケーション全体で単一のデータベースインスタンスを共有します。
         * synchronized ブロックを使用してスレッドセーフなインスタンス作成を保証し、
         * 複数のスレッドから同時に呼び出されても安全です。
         *
         * シングルトンパターンの利点:
         * - メモリ使用量の最適化
         * - データベース接続の効率化
         * - 一貫性のあるデータアクセス
         * - リソースの重複作成防止
         *
         * 初期化処理:
         * - データベースファイルの作成
         * - コールバックによる初期データ投入
         * - DAOインスタンスの準備
         *
         * @param context アプリケーションコンテキスト
         *                データベースファイルの作成場所やアプリ情報に使用
         * @param scope 非同期処理用のCoroutineScope
         *              データベース初期化時のバックグラウンド処理に使用
         * @return AppDatabase アプリケーション全体で共有されるデータベースインスタンス
         */
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // 既存のインスタンスがあればそれを返す（パフォーマンス向上）
            return INSTANCE ?: synchronized(this) {
                // Roomデータベースビルダーでインスタンスを作成
                val instance = Room.databaseBuilder(
                    context.applicationContext,    // アプリケーションコンテキスト
                    AppDatabase::class.java,       // データベースクラス
                    "app_database"                 // データベースファイル名
                )
                    .addCallback(AppDatabaseCallback(scope))  // 初期化コールバック追加
                    .build()

                // 作成したインスタンスをキャッシュして返す
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * データベース作成時のコールバッククラス
     *
     * データベースが初めて作成されたときに実行される処理を定義します。
     * アプリの初回起動時にデフォルトデータを挿入するために使用されます。
     *
     * コールバックの利点:
     * - データベース作成タイミングでの自動実行
     * - アプリの初期状態の保証
     * - 開発とテストの効率化
     * - データの一貫性確保
     *
     * @property scope 非同期処理用のCoroutineScope
     *                 データベース初期化時のバックグラウンド処理に使用
     */
    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        /**
         * データベースが作成されたときに呼び出されるメソッド
         *
         * アプリの初回起動時やデータベースファイルが削除された後の
         * 再作成時に自動実行されます。ここで初期データの投入を行います。
         *
         * 処理の流れ:
         * 1. 既存データの削除（クリーンな状態を保証）
         * 2. 「Android」文字列の挿入
         * 3. メイン画面での表示準備完了
         *
         * 非同期処理:
         * - コルーチンを使用してメインスレッドをブロックしない
         * - データベース操作は時間がかかる可能性があるため
         * - UIの応答性を保持
         *
         * @param db データベースインスタンス（Room内部で使用、直接操作は不要）
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // データベースインスタンスが利用可能な場合のみ初期化処理を実行
            INSTANCE?.let { database ->
                scope.launch {
                    // StringDaoを取得
                    val stringDao = database.stringDao()

                    // 既存データを削除（クリーンな初期状態を保証）
                    stringDao.deleteAllStrings()

                    // 「Android」文字列を挿入（メイン画面で表示される文字列）
                    stringDao.insertString(StringEntity(value = "Android"))
                }
            }
        }
    }
}
