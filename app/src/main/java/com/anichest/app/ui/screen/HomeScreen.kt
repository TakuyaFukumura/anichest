package com.anichest.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.viewmodel.AnimeListViewModel

/**
 * ホーム画面
 * 
 * アニメの統計情報を表示するアプリのメイン画面です。
 * 視聴中・完了のアニメ数、ウィッシュリスト数の統計と、
 * 各カテゴリへのナビゲーション機能を提供します。
 * アニメ情報追加用のFloatingActionButtonも提供します。
 * 
 * @param viewModel アニメリスト情報を提供するViewModel
 * @param onNavigateToAnimeList アニメリスト画面への遷移コールバック
 * @param onNavigateToWishlist ウィッシュリスト画面への遷移コールバック
 * @param onNavigateToAddWishlist アニメ追加画面への遷移コールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AnimeListViewModel,
    onNavigateToAnimeList: (WatchStatus?) -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onNavigateToAddWishlist: () -> Unit = {}
) {
    val animeList by viewModel.animeList.collectAsState(initial = emptyList())
    val watchingCount by viewModel.watchingCount.collectAsState(initial = 0)
    val completedCount by viewModel.completedCount.collectAsState(initial = 0)
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddWishlist
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "アニメ情報を追加"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Anichest",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 統計カード
                StatsSection(
                    watchingCount = watchingCount,
                    completedCount = completedCount,
                    totalCount = animeList.size,
                    onNavigateToAnimeList = onNavigateToAnimeList,
                    onNavigateToWishlist = onNavigateToWishlist
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    watchingCount: Int,
    completedCount: Int,
    totalCount: Int,
    onNavigateToAnimeList: (WatchStatus?) -> Unit,
    onNavigateToWishlist: () -> Unit
) {
    Column {
        Text(
            text = "統計",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "視聴中",
                count = watchingCount,
                icon = Icons.Filled.PlayArrow,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onNavigateToAnimeList(WatchStatus.WATCHING) }
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "完了",
                count = completedCount,
                icon = Icons.Filled.Star,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { onNavigateToAnimeList(WatchStatus.COMPLETED) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "合計",
                count = totalCount,
                icon = Icons.Filled.Favorite,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { onNavigateToAnimeList(null) }
            )

            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = onNavigateToWishlist
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ウィッシュリスト",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


