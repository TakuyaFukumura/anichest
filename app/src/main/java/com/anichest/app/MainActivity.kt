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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anichest.app.ui.navigation.NavigationDestination
import com.anichest.app.ui.screen.AnimeDetailScreen
import com.anichest.app.ui.screen.AnimeListScreen
import com.anichest.app.ui.screen.HomeScreen
import com.anichest.app.ui.screen.WishlistScreen
import com.anichest.app.ui.theme.AnichestTheme
import com.anichest.app.ui.viewmodel.AnimeDetailViewModel
import com.anichest.app.ui.viewmodel.AnimeDetailViewModelFactory
import com.anichest.app.ui.viewmodel.AnimeListViewModel
import com.anichest.app.ui.viewmodel.AnimeListViewModelFactory
import com.anichest.app.ui.viewmodel.WishlistViewModel
import com.anichest.app.ui.viewmodel.WishlistViewModelFactory

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

    // ナビゲーションの状態管理
    var currentDestination by remember { mutableStateOf<NavigationDestination>(NavigationDestination.Home) }

    if (application != null) {
        // ナビゲーション関数
        val navigateToHome = { currentDestination = NavigationDestination.Home }
        val navigateToAnimeList = { currentDestination = NavigationDestination.AnimeList }
        val navigateToWishlist = { currentDestination = NavigationDestination.Wishlist }
        val navigateToAnimeDetail = { animeId: Int ->
            currentDestination = NavigationDestination.AnimeDetail(animeId)
        }

        // 現在の画面に応じてコンテンツを表示
        when (currentDestination) {
            NavigationDestination.Home -> {
                val viewModel: AnimeListViewModel = viewModel(
                    factory = AnimeListViewModelFactory(
                        application.animeRepository,
                        application.animeStatusRepository
                    )
                )

                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAnimeList = navigateToAnimeList,
                    onNavigateToWishlist = navigateToWishlist,
                    onNavigateToAnimeDetail = navigateToAnimeDetail
                )
            }

            NavigationDestination.AnimeList -> {
                val viewModel: AnimeListViewModel = viewModel(
                    factory = AnimeListViewModelFactory(
                        application.animeRepository,
                        application.animeStatusRepository
                    )
                )

                AnimeListScreen(
                    viewModel = viewModel,
                    onNavigateBack = navigateToHome,
                    onNavigateToAnimeDetail = navigateToAnimeDetail
                )
            }

            NavigationDestination.Wishlist -> {
                val viewModel: WishlistViewModel = viewModel(
                    factory = WishlistViewModelFactory(
                        application.wishlistRepository
                    )
                )

                WishlistScreen(
                    viewModel = viewModel,
                    onNavigateBack = navigateToHome,
                    onNavigateToAnimeDetail = navigateToAnimeDetail
                )
            }

            is NavigationDestination.AnimeDetail -> {
                val viewModel: AnimeDetailViewModel = viewModel(
                    factory = AnimeDetailViewModelFactory(
                        application.animeRepository,
                        application.animeStatusRepository
                    )
                )

                val animeDetailDestination = currentDestination as NavigationDestination.AnimeDetail
                AnimeDetailScreen(
                    animeId = animeDetailDestination.animeId,
                    viewModel = viewModel,
                    onNavigateBack = navigateToHome
                )
            }
        }
    } else {
        // エラー時の表示は簡潔に
        Text(
            text = "アプリケーションの初期化に失敗しました",
            modifier = modifier
        )
    }
}


