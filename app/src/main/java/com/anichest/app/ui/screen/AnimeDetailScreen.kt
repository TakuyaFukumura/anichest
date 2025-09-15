package com.anichest.app.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
    val isEditing by viewModel.isEditing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // アニメデータをロード
    LaunchedEffect(animeId) {
        viewModel.loadAnime(animeId)
    }

    // エラー表示
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
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
                    if (anime != null && !isLoading) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "削除")
                        }
                        if (isEditing) {
                            IconButton(onClick = { viewModel.setEditMode(false) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "編集完了")
                            }
                        } else {
                            IconButton(onClick = { viewModel.setEditMode(true) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "編集")
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                anime != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isEditing) {
                            EditAnimeCard(
                                title = anime!!.title,
                                totalEpisodes = anime!!.totalEpisodes,
                                genre = anime!!.genre,
                                year = anime!!.year,
                                description = anime!!.description,
                                watchStatus = animeStatus?.status ?: WatchStatus.UNWATCHED,
                                rating = animeStatus?.rating ?: 0,
                                review = animeStatus?.review ?: "",
                                watchedEpisodes = animeStatus?.watchedEpisodes ?: 0,
                                onSave = { title, totalEpisodes, genre, year, description, watchStatus, rating, review, watchedEpisodes ->
                                    viewModel.updateAnimeAndStatus(
                                        title = title,
                                        totalEpisodes = totalEpisodes,
                                        genre = genre,
                                        year = year,
                                        description = description,
                                        status = watchStatus,
                                        rating = rating,
                                        review = review,
                                        watchedEpisodes = watchedEpisodes
                                    )
                                },
                                onCancel = { viewModel.setEditMode(false) }
                            )
                        } else {
                            AnimeInfoCard(
                                title = anime!!.title,
                                year = anime!!.year,
                                genre = anime!!.genre,
                                totalEpisodes = anime!!.totalEpisodes,
                                description = anime!!.description
                            )

                            // 視聴状況カードを追加
                            WatchStatusCard(
                                watchStatus = animeStatus?.status ?: WatchStatus.UNWATCHED,
                                rating = animeStatus?.rating ?: 0,
                                review = animeStatus?.review ?: "",
                                watchedEpisodes = animeStatus?.watchedEpisodes ?: 0,
                                totalEpisodes = anime!!.totalEpisodes
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        text = "アニメが見つかりませんでした",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // 削除確認ダイアログ
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("削除確認") },
            text = { Text("このアニメを削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAnime()
                        showDeleteDialog = false
                        onNavigateBack()
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // タイトル
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 年とジャンル
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${year}年",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                if (genre.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = genre,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 話数
            if (totalEpisodes > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "全${totalEpisodes}話",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 説明
            if (description.isNotBlank()) {
                Text(
                    text = "あらすじ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3f
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAnimeCard(
    title: String,
    totalEpisodes: Int,
    genre: String,
    year: Int,
    description: String,
    watchStatus: WatchStatus,
    rating: Int,
    review: String,
    watchedEpisodes: Int,
    onSave: (String, Int, String, Int, String, WatchStatus, Int, String, Int) -> Unit,
    onCancel: () -> Unit
) {
    var editedTitle by remember { mutableStateOf(title) }
    var editedTotalEpisodes by remember { mutableStateOf(totalEpisodes.toString()) }
    var editedGenre by remember { mutableStateOf(genre) }
    var editedYear by remember { mutableStateOf(year.toString()) }
    var editedDescription by remember { mutableStateOf(description) }
    var editedWatchStatus by remember { mutableStateOf(watchStatus) }
    var editedRating by remember { mutableStateOf(rating) }
    var editedReview by remember { mutableStateOf(review) }
    var editedWatchedEpisodes by remember { mutableStateOf(watchedEpisodes.toString()) }

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
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = editedTitle,
                onValueChange = { editedTitle = it },
                label = { Text("タイトル") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = editedTotalEpisodes,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("\\d*"))) editedTotalEpisodes = it
                },
                label = { Text("全話数") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = editedGenre,
                onValueChange = { editedGenre = it },
                label = { Text("ジャンル") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = editedYear,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("\\d{0,4}"))) editedYear = it
                },
                label = { Text("放送年") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = editedDescription,
                onValueChange = { editedDescription = it },
                label = { Text("説明・あらすじ") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 視聴ステータス選択
            var watchStatusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = watchStatusExpanded,
                onExpandedChange = { watchStatusExpanded = !watchStatusExpanded }
            ) {
                OutlinedTextField(
                    value = WatchStatusUtils.getWatchStatusText(editedWatchStatus),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("視聴ステータス") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = watchStatusExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = watchStatusExpanded,
                    onDismissRequest = { watchStatusExpanded = false }
                ) {
                    WatchStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(WatchStatusUtils.getWatchStatusText(status)) },
                            onClick = {
                                editedWatchStatus = status
                                watchStatusExpanded = false
                            }
                        )
                    }
                }
            }

            // 評価選択
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "評価",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (editedRating == 0) "未評価" else "$editedRating / 5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Slider(
                        value = editedRating.toFloat(),
                        onValueChange = { editedRating = it.toInt() },
                        valueRange = 0f..5f,
                        steps = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "未評価",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "5",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // レビュー入力
            OutlinedTextField(
                value = editedReview,
                onValueChange = { editedReview = it },
                label = { Text("レビュー・感想") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 視聴済み話数入力
            OutlinedTextField(
                value = editedWatchedEpisodes,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("\\d*"))) editedWatchedEpisodes = it
                },
                label = { Text("視聴済み話数") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onSave(
                            editedTitle,
                            editedTotalEpisodes.toIntOrNull() ?: 0,
                            editedGenre,
                            editedYear.toIntOrNull() ?: 0,
                            editedDescription,
                            editedWatchStatus,
                            editedRating,
                            editedReview,
                            editedWatchedEpisodes.toIntOrNull() ?: 0
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = editedTitle.isNotBlank()
                ) {
                    Text("保存")
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("キャンセル")
                }
            }
        }
    }
}

/**
 * 視聴状況を表示するカード
 */
@Composable
private fun WatchStatusCard(
    watchStatus: WatchStatus,
    rating: Int,
    review: String,
    watchedEpisodes: Int,
    totalEpisodes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "視聴状況",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // ステータス表示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ステータス",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    color = getStatusColor(watchStatus)
                ) {
                    Text(
                        text = WatchStatusUtils.getWatchStatusText(watchStatus),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // 進捗表示（全話数がある場合のみ）
            if (totalEpisodes > 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "進捗",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "$watchedEpisodes / $totalEpisodes 話",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = { 
                            if (totalEpisodes > 0) watchedEpisodes.toFloat() / totalEpisodes.toFloat()
                            else 0f 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else if (watchedEpisodes > 0) {
                // 全話数不明だが視聴済み話数がある場合
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "視聴済み",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "${watchedEpisodes}話",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // 評価表示
            if (rating > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "評価",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    StarRating(rating = rating)
                }
            }

            // レビュー表示
            if (review.isNotBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "レビュー",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = review,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

/**
 * 星評価を表示するコンポーネント
 */
@Composable
private fun StarRating(
    rating: Int,
    maxRating: Int = 5
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$rating/5",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

/**
 * 視聴ステータスに応じた色を取得
 */
@Composable
private fun getStatusColor(status: WatchStatus) = when (status) {
    WatchStatus.UNWATCHED -> MaterialTheme.colorScheme.outline
    WatchStatus.WATCHING -> MaterialTheme.colorScheme.primary
    WatchStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
    WatchStatus.DROPPED -> MaterialTheme.colorScheme.error
}
