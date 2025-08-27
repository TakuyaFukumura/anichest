package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.MainViewModel
import com.example.myapplication.ui.viewmodel.MainViewModelFactory

/**
 * アプリケーションのメインアクティビティ
 *
 * Androidアプリの最初に起動されるコンポーネントです。
 * アプリのUIを構築し、Jetpack Composeを使用して画面を表示します。
 *
 * 主な機能:
 * - Edge-to-edge表示の有効化
 * - Composeテーマの適用
 * - メイン画面の表示
 */
class MainActivity : ComponentActivity() {

    /**
     * アクティビティが作成されたときに呼び出されるメソッド
     *
     * Androidシステムがアクティビティのライフサイクル中で最初に呼び出すメソッドです。
     * ここでUIの初期化を行います。
     *
     * @param savedInstanceState 以前のアクティビティ状態の保存データ。
     *                          アプリが初回起動の場合はnullになります。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge表示を有効化（ステータスバーやナビゲーションバーの領域も使用）
        enableEdgeToEdge()

        // Jetpack ComposeのUIを設定
        setContent {
            // アプリのテーマを適用
            MyApplicationTheme {
                // Material3のScaffoldを使用してレイアウトの基本構造を作成
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // メイン画面を表示（パディングを適用してシステムバーとの重複を回避）
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * メイン画面を構成するComposable関数
 *
 * アプリのメインコンテンツを表示します。
 * データベースから文字列を取得してViewModelを通じて画面に表示します。
 *
 * 技術的な詳細:
 * - ApplicationインスタンスからStringRepositoryを取得
 * - ViewModelFactoryを使用してMainViewModelを作成
 * - StateFlowを使用してデータの変更を監視
 * - 安全なキャストでエラーハンドリングを実装
 *
 * @param modifier UIコンポーネントのレイアウト調整用修飾子。
 *                デフォルト値はModifier（何も変更しない）です。
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Application インスタンスからリポジトリを取得（安全なキャストを使用）
    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val application = context as? MyApplication

    if (application != null) {
        // ViewModelを作成（ファクトリーを使用）
        val viewModel: MainViewModel = viewModel(
            factory = MainViewModelFactory(application.repository)
        )
        // StateFlowから文字列を取得
        val greeting by viewModel.greeting.collectAsState()
        Greeting(
            name = greeting,
            modifier = modifier
        )
    } else {
        // キャスト失敗時はエラーメッセージを表示
        Greeting(
            name = "Error: Invalid Application Context",
            modifier = modifier
        )
    }
}

/**
 * 挨拶メッセージを表示するComposable関数
 *
 * 渡された名前を「Hello 名前!」の形式で表示します。
 * シンプルなテキスト表示コンポーネントです。
 *
 * @param name 表示する名前（通常は「Android」）
 * @param modifier UIコンポーネントのレイアウト調整用修飾子。
 *                デフォルト値はModifier（何も変更しない）です。
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/**
 * Greeting Composableのプレビュー用関数
 *
 * Android Studioのデザインタブでレイアウトを確認するために使用します。
 * 実際のアプリ実行時には呼び出されません。
 *
 * 表示内容: "Hello Android!" というテキスト
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
