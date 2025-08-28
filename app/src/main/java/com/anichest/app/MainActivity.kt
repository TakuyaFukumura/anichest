package com.anichest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anichest.app.ui.screen.HomeScreen
import com.anichest.app.ui.theme.AnichestTheme
import com.anichest.app.ui.viewmodel.AnimeListViewModel
import com.anichest.app.ui.viewmodel.AnimeListViewModelFactory

/**
 * アニメ視聴管理アプリ「Anichest」のメインアクティビティ
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AnichestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val application = context as? AnichestApplication

    if (application != null) {
        val viewModel: AnimeListViewModel = viewModel(
            factory = AnimeListViewModelFactory(
                application.animeRepository,
                application.animeStatusRepository
            )
        )
        
        HomeScreen(
            viewModel = viewModel,
            onNavigateToAnimeList = { /* TODO: ナビゲーション実装 */ },
            onNavigateToWishlist = { /* TODO: ナビゲーション実装 */ },
            onNavigateToAnimeDetail = { /* TODO: ナビゲーション実装 */ }
        )
    } else {
        // エラー時の表示は簡潔に
        Text(
            text = "アプリケーションの初期化に失敗しました",
            modifier = modifier
        )
    }
}


