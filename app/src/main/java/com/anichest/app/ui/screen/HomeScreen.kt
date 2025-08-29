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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.AnimeWithStatus
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.viewmodel.AnimeListViewModel

/**
 * ホーム画面
 * アニメの統計情報と最近の活動を表示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AnimeListViewModel,
    onNavigateToAnimeList: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onNavigateToAnimeDetail: (Int) -> Unit = {}
) {
    val animeList by viewModel.animeList.collectAsState(initial = emptyList())
    val watchingCount by viewModel.watchingCount.collectAsState(initial = 0)
    val completedCount by viewModel.completedCount.collectAsState(initial = 0)
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
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

            Spacer(modifier = Modifier.height(24.dp))

            // 最近の活動
            RecentActivitySection(
                animeList = animeList.filter { it.status != null }
                    .sortedByDescending { it.status?.updatedAt }
                    .take(5),
                onAnimeClick = onNavigateToAnimeDetail
            )
        }
    }
}

@Composable
private fun StatsSection(
    watchingCount: Int,
    completedCount: Int,
    totalCount: Int,
    onNavigateToAnimeList: () -> Unit,
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
                onClick = onNavigateToAnimeList
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "完了",
                count = completedCount,
                icon = Icons.Filled.Star,
                color = MaterialTheme.colorScheme.secondary,
                onClick = onNavigateToAnimeList
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
                onClick = onNavigateToAnimeList
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

@Composable
private fun RecentActivitySection(
    animeList: List<AnimeWithStatus>,
    onAnimeClick: (Int) -> Unit
) {
    Column {
        Text(
            text = "最近の活動",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (animeList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "まだアニメが登録されていません",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(animeList) { animeWithStatus ->
                    RecentAnimeItem(
                        animeWithStatus = animeWithStatus,
                        onClick = { onAnimeClick(animeWithStatus.anime.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentAnimeItem(
    animeWithStatus: AnimeWithStatus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = animeWithStatus.anime.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                animeWithStatus.status?.let { status ->
                    Text(
                        text = getStatusText(status.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (status.watchedEpisodes > 0) {
                        Text(
                            text = "${status.watchedEpisodes}話視聴",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            animeWithStatus.status?.let { status ->
                if (status.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = status.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getStatusText(status: WatchStatus): String {
    return when (status) {
        WatchStatus.UNWATCHED -> "未視聴"
        WatchStatus.WATCHING -> "視聴中"
        WatchStatus.COMPLETED -> "視聴済"
        WatchStatus.DROPPED -> "中止"
    }
}
