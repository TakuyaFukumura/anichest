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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.AnimeWithStatus
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.viewmodel.AnimeListViewModel

/**
 * アニメリスト画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    viewModel: AnimeListViewModel,
    filter: WatchStatus? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToAnimeDetail: (Long) -> Unit = {}
) {
    val animeList by viewModel.animeList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // フィルターが指定されている場合は設定
    LaunchedEffect(filter) {
        viewModel.setFilter(filter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("アニメリスト") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 検索フィールド
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("アニメを検索") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // フィルタリングされたアニメリスト
                val filteredAnimeList = if (searchQuery.isBlank()) {
                    animeList
                } else {
                    animeList.filter {
                        it.anime.title.contains(searchQuery, ignoreCase = true)
                    }
                }

                if (filteredAnimeList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) {
                                "アニメが登録されていません"
                            } else {
                                "検索結果が見つかりません"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredAnimeList) { animeWithStatus ->
                            AnimeListItem(
                                animeWithStatus = animeWithStatus,
                                onClick = { onNavigateToAnimeDetail(animeWithStatus.anime.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeListItem(
    animeWithStatus: AnimeWithStatus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = animeWithStatus.anime.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

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

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${animeWithStatus.anime.year}年 | ${animeWithStatus.anime.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                animeWithStatus.status?.let { status ->
                    Text(
                        text = getStatusText(status.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = getStatusColor(status.status)
                    )
                }
            }

            if (animeWithStatus.anime.totalEpisodes > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                val watchedEpisodes = animeWithStatus.status?.watchedEpisodes ?: 0
                Text(
                    text = "$watchedEpisodes / ${animeWithStatus.anime.totalEpisodes}話",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

@Composable
private fun getStatusColor(status: WatchStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        WatchStatus.UNWATCHED -> MaterialTheme.colorScheme.onSurfaceVariant
        WatchStatus.WATCHING -> MaterialTheme.colorScheme.primary
        WatchStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
        WatchStatus.DROPPED -> MaterialTheme.colorScheme.error
    }
}