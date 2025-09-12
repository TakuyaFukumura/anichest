package com.anichest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.navigation.NavigationDestination
import com.anichest.app.ui.screen.AddWishlistScreen
import com.anichest.app.ui.screen.AnimeDetailScreen
import com.anichest.app.ui.screen.AnimeListScreen
import com.anichest.app.ui.screen.HomeScreen
import com.anichest.app.ui.screen.WishlistScreen
import com.anichest.app.ui.theme.AnichestTheme
import com.anichest.app.ui.viewmodel.AddWishlistViewModel
import com.anichest.app.ui.viewmodel.AnimeDetailViewModel
import com.anichest.app.ui.viewmodel.AnimeListViewModel
import com.anichest.app.ui.viewmodel.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * アニメ視聴管理アプリ「Anichest」のメインアクティビティ
 * 
 * アプリの起動とJetpack Composeによる画面レンダリングを担当します。
 * Hilt DIコンテナのエントリーポイントとしても機能します。
 * 
 * @see NavigationDestination
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Activityの作成時に呼び出されるメソッド
     * Edge-to-Edgeの有効化とComposeコンテンツの設定を行います
     * 
     * @param savedInstanceState 保存されたインスタンス状態
     */
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

/**
 * アプリのメイン画面を管理するComposable関数
 * 
 * 画面間のナビゲーション状態を管理し、現在の画面に応じて
 * 適切なスクリーンコンポーネントを表示します。
 * 各画面への遷移関数も定義・提供します。
 * 
 * @param modifier レイアウト調整用のModifier
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // ナビゲーションの状態管理
    var currentDestination by remember { mutableStateOf<NavigationDestination>(NavigationDestination.Home) }

    // ナビゲーション関数の定義
    val navigateToHome = { currentDestination = NavigationDestination.Home }
    val navigateToAnimeList = { filter: WatchStatus? ->
        currentDestination = NavigationDestination.AnimeList(filter)
    }
    val navigateToWishlist = { currentDestination = NavigationDestination.Wishlist }
    val navigateToAddWishlist = { currentDestination = NavigationDestination.AddWishlist }
    val navigateToAnimeDetail = { animeId: Long ->
        currentDestination = NavigationDestination.AnimeDetail(animeId)
    }

    // 現在の画面に応じてコンテンツを表示
    when (currentDestination) {
        NavigationDestination.Home -> {
            val viewModel: AnimeListViewModel = hiltViewModel()

            HomeScreen(
                viewModel = viewModel,
                onNavigateToAnimeList = { filter -> navigateToAnimeList(filter) },
                onNavigateToWishlist = navigateToWishlist,
                onNavigateToAddWishlist = navigateToAddWishlist
            )
        }

        is NavigationDestination.AnimeList -> {
            val viewModel: AnimeListViewModel = hiltViewModel()
            val animeListDestination = currentDestination as NavigationDestination.AnimeList

            AnimeListScreen(
                viewModel = viewModel,
                filter = animeListDestination.filter,
                onNavigateBack = navigateToHome,
                onNavigateToAnimeDetail = navigateToAnimeDetail
            )
        }

        NavigationDestination.Wishlist -> {
            val viewModel: WishlistViewModel = hiltViewModel()

            WishlistScreen(
                viewModel = viewModel,
                onNavigateBack = navigateToHome,
                onNavigateToAnimeDetail = navigateToAnimeDetail
            )
        }

        NavigationDestination.AddWishlist -> {
            val viewModel: AddWishlistViewModel = hiltViewModel()

            AddWishlistScreen(
                viewModel = viewModel,
                onNavigateBack = navigateToWishlist
            )
        }

        is NavigationDestination.AnimeDetail -> {
            val viewModel: AnimeDetailViewModel = hiltViewModel()

            val animeDetailDestination = currentDestination as NavigationDestination.AnimeDetail
            AnimeDetailScreen(
                animeId = animeDetailDestination.animeId,
                viewModel = viewModel,
                onNavigateBack = navigateToHome
            )
        }
    }
}
