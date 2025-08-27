package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.StringRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * メイン画面のViewModel
 *
 * MVVM（Model-View-ViewModel）アーキテクチャパターンのViewModel層を実装しています。
 * UI（View）とデータ（Model）の間を仲介し、UIに表示するデータの管理と
 * ビジネスロジックの処理を担当します。
 *
 * 主な責務:
 * - データベースから文字列データを取得
 * - UI状態の管理（Loading、成功、エラー）
 * - Compose UIとのリアクティブな連携
 * - ライフサイクルを考慮した非同期処理
 *
 * StateFlowの利点:
 * - UIスレッドでの状態変更通知
 * - 最新の状態値の保持
 * - 設定変更（画面回転など）での状態維持
 * - Composeとの自動的な再描画連携
 *
 * @property repository データベースアクセスを提供するStringRepository
 */
class MainViewModel(private val repository: StringRepository) : ViewModel() {

    /**
     * 内部で管理される挨拶メッセージの状態
     *
     * MutableStateFlowを使用してUIの状態を管理します。
     * privateにすることで外部からの直接変更を防ぎ、
     * ViewModelを通じてのみ状態変更が可能になります。
     *
     * 初期値: "Loading..." - データ取得中であることを示す
     */
    private val _greeting = MutableStateFlow("Loading...")

    /**
     * UI側からアクセス可能な挨拶メッセージの状態
     *
     * StateFlowとして公開することで:
     * - 読み取り専用アクセスを提供
     * - Composeでの自動再描画を可能にする
     * - 状態の変更を安全に監視
     *
     * Compose側では collectAsState() を使用してこの値を監視し、
     * 状態が変更されると自動的にUIが再描画されます。
     */
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    /**
     * ViewModelの初期化ブロック
     *
     * ViewModelインスタンスが作成されたときに自動実行されます。
     * ここでデータの初期読み込みを開始し、UIに表示するデータを準備します。
     */
    init {
        loadGreeting()
    }

    /**
     * データベースから挨拶用の文字列を非同期で読み込む
     *
     * リポジトリを通じてデータベースから文字列を取得し、
     * UIの状態を更新します。エラーハンドリングも実装されており、
     * データ取得に失敗した場合はデフォルト値を使用します。
     *
     * 非同期処理の流れ:
     * 1. viewModelScopeでコルーチンを起動
     * 2. repository.getFirstString()でデータベースアクセス
     * 3. 取得結果をStateFlowに反映
     * 4. ComposeUIが自動的に再描画
     *
     * エラーハンドリング:
     * - データベースアクセスエラー: デフォルト値"Android"を使用
     * - データが存在しない場合: デフォルト値"Android"を使用
     * - ユーザーエクスペリエンスを損なわない設計
     *
     * ライフサイクル考慮:
     * - viewModelScopeを使用することで、ViewModelが破棄されると
     *   自動的にコルーチンもキャンセルされる
     * - メモリリークの防止
     * - 適切なリソース管理
     */
    private fun loadGreeting() {
        viewModelScope.launch {
            try {
                // リポジトリを通じてデータベースから最初の文字列を取得
                val stringEntity = repository.getFirstString()
                // 取得した文字列をUIに反映（nullの場合はデフォルト値を使用）
                _greeting.value = stringEntity?.value ?: "Android"
            } catch (_: Exception) {
                // エラーが発生した場合はデフォルト値を使用
                // ログ出力やエラー報告などの追加処理も可能
                _greeting.value = "Android"
            }
        }
    }
}

/**
 * MainViewModelのファクトリークラス
 *
 * ViewModelProvider.Factoryを実装し、MainViewModelのインスタンス作成を管理します。
 * ViewModelには引数付きコンストラクタがあるため、Androidシステムが
 * 直接インスタンス化できません。このファクトリーを使用することで、
 * 依存性注入（StringRepository）を含むViewModelの作成が可能になります。
 *
 * ファクトリーパターンの利点:
 * - 複雑なオブジェクト作成の隠蔽
 * - 依存性注入の実現
 * - テスト時のモック注入が容易
 * - 型安全なインスタンス作成
 *
 * 使用方法:
 * ```kotlin
 * val viewModel: MainViewModel = viewModel(
 *     factory = MainViewModelFactory(repository)
 * )
 * ```
 *
 * @property repository ViewModelに注入するStringRepositoryインスタンス
 */
class MainViewModelFactory(private val repository: StringRepository) : ViewModelProvider.Factory {

    /**
     * 指定されたViewModelクラスのインスタンスを作成する
     *
     * Androidシステムから呼び出され、適切なViewModelインスタンスを返します。
     * 型安全性を保つため、要求されたクラスがMainViewModelかどうかを
     * チェックしてから作成を行います。
     *
     * ジェネリクス:
     * - <T : ViewModel> により、ViewModelのサブクラスのみ受け入れ
     * - 型安全なキャストを実現
     * - コンパイル時の型チェック
     *
     * @param modelClass 作成するViewModelのClassオブジェクト
     * @return T 要求されたViewModelのインスタンス
     * @throws IllegalArgumentException 未対応のViewModelクラスが指定された場合
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 要求されたクラスがMainViewModelかどうかをチェック
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // 型安全なキャストでMainViewModelインスタンスを返す
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        // 未対応のViewModelクラスの場合は例外をスロー
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
