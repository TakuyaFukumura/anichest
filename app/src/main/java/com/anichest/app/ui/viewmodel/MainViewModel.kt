package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.repository.StringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: StringRepository) : ViewModel() {

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
