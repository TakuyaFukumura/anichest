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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.viewmodel.AnimeDetailViewModel

/**
 * アニメ詳細画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    animeId: Int,
    viewModel: AnimeDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val anime by viewModel.anime.collectAsState()
    val animeStatus by viewModel.animeStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // アニメデータをロード
    LaunchedEffect(animeId) {
        viewModel.loadAnime(animeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anime?.title ?: "アニメ詳細") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                anime != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // アニメ基本情報
                        AnimeInfoCard(
                            title = anime!!.title,
                            year = anime!!.year,
                            genre = anime!!.genre,
                            totalEpisodes = anime!!.totalEpisodes,
                            description = anime!!.description
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 視聴ステータス情報
                        animeStatus?.let { status ->
                            StatusCard(
                                status = status.status,
                                watchedEpisodes = status.watchedEpisodes,
                                totalEpisodes = anime!!.totalEpisodes,
                                rating = status.rating,
                                review = status.review
                            )
                        } ?: run {
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
                                        text = "まだ視聴ステータスが設定されていません",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeInfoCard(
    title: String,
    year: Int,
    genre: String,
    totalEpisodes: Int,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${year}年",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = genre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (totalEpisodes > 0) {
                    Text(
                        text = "全${totalEpisodes}話",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    status: WatchStatus,
    watchedEpisodes: Int,
    totalEpisodes: Int,
    rating: Int,
    review: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "視聴ステータス",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getStatusText(status),
                    style = MaterialTheme.typography.bodyLarge,
                    color = getStatusColor(status)
                )

                if (rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "$rating/5",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            if (totalEpisodes > 0 && watchedEpisodes > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "進行状況: $watchedEpisodes / $totalEpisodes 話",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (review.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "レビュー",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = review,
                    style = MaterialTheme.typography.bodyMedium
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