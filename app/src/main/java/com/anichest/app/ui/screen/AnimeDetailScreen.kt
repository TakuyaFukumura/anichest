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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.util.WatchStatusUtils
import com.anichest.app.ui.viewmodel.AnimeDetailViewModel

/**
 * アニメ詳細画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    animeId: Long,
    viewModel: AnimeDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val anime by viewModel.anime.collectAsState()
    val animeStatus by viewModel.animeStatus.collectAsState()
    val wishlistItem by viewModel.wishlistItem.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()

    // 削除確認ダイアログの表示状態
    var showDeleteDialog by remember { mutableStateOf(false) }

    // アニメデータをロード
    LaunchedEffect(animeId) {
        viewModel.loadAnime(animeId)
    }

    // 削除成功時のナビゲーション
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anime?.title ?: "アニメ詳細") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // 削除ボタン（アニメが存在する場合）
                    if (anime != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "削除")
                        }
                    }
                    
                    // ウィッシュリストにあるアニメの場合、編集ボタンを表示
                    if (wishlistItem != null) {
                        if (isEditing) {
                            IconButton(onClick = { viewModel.setEditing(false) }) {
                                Icon(Icons.Filled.Close, contentDescription = "編集をキャンセル")
                            }
                        } else {
                            IconButton(onClick = { viewModel.setEditing(true) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "編集")
                            }
                        }
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
                        // アニメ基本情報（編集モードまたは表示モード）
                        if (isEditing && wishlistItem != null) {
                            EditableAnimeInfoCard(
                                anime = anime!!,
                                wishlistItem = wishlistItem!!,
                                animeStatus = animeStatus,
                                onAnimeUpdate = { title, totalEpisodes, genre, year, description ->
                                    viewModel.updateAnime(
                                        title,
                                        totalEpisodes,
                                        genre,
                                        year,
                                        description
                                    )
                                },
                                onWishlistUpdate = { priority, notes ->
                                    viewModel.updateWishlistItem(priority, notes)
                                },
                                onStatusUpdate = { watchStatus ->
                                    viewModel.updateAnimeStatus(
                                        status = watchStatus,
                                        rating = animeStatus?.rating ?: 0,
                                        review = animeStatus?.review ?: "",
                                        watchedEpisodes = animeStatus?.watchedEpisodes ?: 0
                                    )
                                }
                            )
                        } else {
                            AnimeInfoCard(
                                title = anime!!.title,
                                year = anime!!.year,
                                genre = anime!!.genre,
                                totalEpisodes = anime!!.totalEpisodes,
                                description = anime!!.description
                            )

                            // ウィッシュリスト情報の表示
                            wishlistItem?.let { wishlist ->
                                Spacer(modifier = Modifier.height(16.dp))
                                WishlistInfoCard(
                                    priority = wishlist.priority,
                                    notes = wishlist.notes,
                                    addedAt = wishlist.addedAt
                                )
                            }
                        }

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

    // 削除確認ダイアログ
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("削除の確認") },
            text = {
                val displayTitle = anime?.title ?: "タイトル不明のアニメ"
                Text("「$displayTitle」を削除しますか？\nこの操作は取り消せません。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAnime()
                    }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
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

@Composable
private fun WishlistInfoCard(
    priority: Priority,
    notes: String,
    addedAt: Long
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
                text = "ウィッシュリスト情報",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "優先度: ${getPriorityText(priority)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "メモ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableAnimeInfoCard(
    anime: com.anichest.app.data.entity.Anime,
    wishlistItem: com.anichest.app.data.entity.WishlistItem,
    animeStatus: com.anichest.app.data.entity.AnimeStatus?,
    onAnimeUpdate: (String, Int, String, Int, String) -> Unit,
    onWishlistUpdate: (Priority, String) -> Unit,
    onStatusUpdate: (WatchStatus) -> Unit
) {
    var title by remember { mutableStateOf(anime.title) }
    var totalEpisodes by remember { mutableStateOf(anime.totalEpisodes.toString()) }
    var genre by remember { mutableStateOf(anime.genre) }
    var year by remember { mutableStateOf(anime.year.toString()) }
    var description by remember { mutableStateOf(anime.description) }
    var priority by remember { mutableStateOf(wishlistItem.priority) }
    var notes by remember { mutableStateOf(wishlistItem.notes) }
    var watchStatus by remember { mutableStateOf(animeStatus?.status ?: WatchStatus.UNWATCHED) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "アニメ情報編集",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // アニメタイトル
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("タイトル") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 全話数
                OutlinedTextField(
                    value = totalEpisodes,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("\\d*"))) {
                            totalEpisodes = it
                        }
                    },
                    label = { Text("全話数") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                // 放送年
                OutlinedTextField(
                    value = year,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("\\d*"))) {
                            year = it
                        }
                    },
                    label = { Text("放送年") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // ジャンル
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("ジャンル") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 説明
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("説明・あらすじ") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Text(
                text = "ウィッシュリスト設定",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 優先度選択
            var priorityExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded }
            ) {
                OutlinedTextField(
                    value = getPriorityText(priority),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("優先度") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    Priority.entries.forEach { priorityOption ->
                        DropdownMenuItem(
                            text = { Text(getPriorityText(priorityOption)) },
                            onClick = {
                                priority = priorityOption
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "視聴ステータス設定",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 視聴ステータス選択
            var watchStatusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = watchStatusExpanded,
                onExpandedChange = { watchStatusExpanded = !watchStatusExpanded }
            ) {
                OutlinedTextField(
                    value = WatchStatusUtils.getWatchStatusText(watchStatus),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("視聴ステータス") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = watchStatusExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = watchStatusExpanded,
                    onDismissRequest = { watchStatusExpanded = false }
                ) {
                    WatchStatus.entries.forEach { statusOption ->
                        DropdownMenuItem(
                            text = { Text(WatchStatusUtils.getWatchStatusText(statusOption)) },
                            onClick = {
                                watchStatus = statusOption
                                watchStatusExpanded = false
                            }
                        )
                    }
                }
            }

            // メモ
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("メモ・備考") },
                placeholder = { Text("このアニメについてのメモを入力（任意）") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // 保存ボタン
            Button(
                onClick = {
                    onAnimeUpdate(
                        title.trim(),
                        totalEpisodes.toIntOrNull() ?: 0,
                        genre.trim(),
                        year.toIntOrNull() ?: 0,
                        description.trim()
                    )
                    onWishlistUpdate(priority, notes.trim())
                    onStatusUpdate(watchStatus)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("保存")
            }
        }
    }
}

private fun getPriorityText(priority: Priority): String {
    return when (priority) {
        Priority.LOW -> "低"
        Priority.MEDIUM -> "中"
        Priority.HIGH -> "高"
    }
}
