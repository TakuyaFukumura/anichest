package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.repository.StringRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * アプリケーションクラス
 *
 * Androidアプリケーション全体のライフサイクルを管理するクラスです。
 * アプリ起動時に一度だけ作成され、アプリが終了するまで存在し続けます。
 *
 * 主な責務:
 * - データベースの初期化と管理
 * - リポジトリインスタンスの提供
 * - アプリケーションスコープのCoroutineScopeの管理
 *
 * このクラスは AndroidManifest.xml で指定する必要があります。
 *
 * アーキテクチャパターン:
 * - MVVMパターンのModel層の基盤
 * - シングルトンパターンでデータベースアクセスを管理
 * - 依存性注入の代替として機能
 */
class MyApplication : Application() {

    /**
     * アプリケーションスコープのCoroutineScope
     *
     * アプリケーション全体で使用される長期間実行されるコルーチンのスコープです。
     * SupervisorJobを使用することで、子コルーチンの例外が
     * 他の子コルーチンに影響しないようにしています。
     *
     * 使用例:
     * - データベースの初期化処理
     * - バックグラウンドでのデータ同期
     * - アプリ全体のライフサイクルに関連する処理
     */
    private val applicationScope = CoroutineScope(SupervisorJob())

    /**
     * データベースのインスタンス（遅延初期化）
     *
     * Roomデータベースのインスタンスを提供します。
     * lazy を使用することで、実際にアクセスされるまで
     * 初期化が遅延され、パフォーマンスが向上します。
     *
     * 初期化時の処理:
     * - データベースファイルの作成
     * - 初期データの投入（"Android"文字列）
     * - DAOインスタンスの準備
     */
    val database by lazy {
        AppDatabase.getDatabase(this, applicationScope)
    }

    /**
     * リポジトリのインスタンス（遅延初期化）
     *
     * StringRepositoryのインスタンスを提供します。
     * リポジトリパターンを実装しており、ViewModelとDAO間の
     * 抽象化レイヤーとして機能します。
     *
     * 利点:
     * - データソースの詳細をViewModelから隠蔽
     * - テスト時のモック作成が容易
     * - 複数のデータソースの統合が可能
     * - ビジネスロジックの集約
     */
    val repository by lazy {
        StringRepository(database.stringDao())
    }
}
