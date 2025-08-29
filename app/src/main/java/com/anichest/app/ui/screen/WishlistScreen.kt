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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.Priority
import com.anichest.app.ui.viewmodel.WishlistViewModel

/**
 * ウィッシュリスト画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToAnimeDetail: (Long) -> Unit = {},
    onNavigateToAddWishlist: () -> Unit = {}
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState(initial = emptyList())
    val selectedPriority by viewModel.selectedPriority.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ウィッシュリスト") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "フィルター")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("すべて") },
                            onClick = {
                                viewModel.clearPriorityFilter()
                                showFilterMenu = false
                            }
                        )
                        Priority.entries.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(getPriorityText(priority)) },
                                onClick = {
                                    viewModel.setPriorityFilter(priority)
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddWishlist
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "ウィッシュリストに追加"
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
            // 優先度フィルターチップ
            if (selectedPriority != null) {
                FilterChip(
                    selected = true,
                    onClick = { viewModel.clearPriorityFilter() },
                    label = { Text(getPriorityText(selectedPriority!!)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (wishlistItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedPriority != null) {
                                "該当する優先度のアニメがありません"
                            } else {
                                "ウィッシュリストが空です"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(wishlistItems) { animeWithWishlist ->
                            WishlistItem(
                                animeWithWishlist = animeWithWishlist,
                                onClick = { onNavigateToAnimeDetail(animeWithWishlist.anime.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistItem(
    animeWithWishlist: AnimeWithWishlist,
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
                    text = animeWithWishlist.anime.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                animeWithWishlist.wishlistItem?.let { wishlistItem ->
                    Text(
                        text = getPriorityText(wishlistItem.priority),
                        style = MaterialTheme.typography.bodySmall,
                        color = getPriorityColor(wishlistItem.priority)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${animeWithWishlist.anime.year}年 | ${animeWithWishlist.anime.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (animeWithWishlist.anime.totalEpisodes > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "全${animeWithWishlist.anime.totalEpisodes}話",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (animeWithWishlist.anime.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = animeWithWishlist.anime.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

private fun getPriorityText(priority: Priority): String {
    return when (priority) {
        Priority.HIGH -> "高優先度"
        Priority.MEDIUM -> "中優先度"
        Priority.LOW -> "低優先度"
    }
}

@Composable
private fun getPriorityColor(priority: Priority): androidx.compose.ui.graphics.Color {
    return when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> MaterialTheme.colorScheme.primary
        Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
}
