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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.viewmodel.AnimeRegistrationViewModel

/**
 * アニメ登録画面
 * 
 * 新しいアニメ作品の基本情報と初期視聴状況を入力して
 * データベースに登録するための画面です。
 * 
 * @param viewModel アニメ登録用のViewModel
 * @param onNavigateBack ホーム画面への戻るナビゲーション
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeRegistrationScreen(
    viewModel: AnimeRegistrationViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val title by viewModel.title.collectAsState()
    val totalEpisodes by viewModel.totalEpisodes.collectAsState()
    val genre by viewModel.genre.collectAsState()
    val year by viewModel.year.collectAsState()
    val description by viewModel.description.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val initialWatchStatus by viewModel.initialWatchStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isRegistrationComplete by viewModel.isRegistrationComplete.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // エラーメッセージ表示
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // 登録完了時の処理
    LaunchedEffect(isRegistrationComplete) {
        if (isRegistrationComplete) {
            snackbarHostState.showSnackbar("アニメ情報を登録しました")
            viewModel.resetForm()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("アニメ登録") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // タイトル入力
                OutlinedTextField(
                    value = title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("タイトル*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                // 全話数入力
                OutlinedTextField(
                    value = totalEpisodes,
                    onValueChange = viewModel::updateTotalEpisodes,
                    label = { Text("全話数") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )

                // ジャンル入力
                OutlinedTextField(
                    value = genre,
                    onValueChange = viewModel::updateGenre,
                    label = { Text("ジャンル") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                // 放送年入力
                OutlinedTextField(
                    value = year,
                    onValueChange = viewModel::updateYear,
                    label = { Text("放送年") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )

                // 作品説明入力
                OutlinedTextField(
                    value = description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("作品説明") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading
                )

                // 画像URL入力
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = viewModel::updateImageUrl,
                    label = { Text("画像URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                // 初期視聴状況選択
                WatchStatusSelectionCard(
                    selectedStatus = initialWatchStatus,
                    onStatusSelected = viewModel::updateInitialWatchStatus,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 登録ボタン
                Button(
                    onClick = viewModel::registerAnime,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && title.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = if (isLoading) "登録中..." else "登録する",
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "* 必須項目",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 視聴状況選択カード
 * 
 * @param selectedStatus 現在選択されている視聴状況
 * @param onStatusSelected 視聴状況選択時のコールバック
 * @param enabled 選択可能かどうか
 */
@Composable
private fun WatchStatusSelectionCard(
    selectedStatus: WatchStatus,
    onStatusSelected: (WatchStatus) -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .selectableGroup()
        ) {
            Text(
                text = "初期視聴状況",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val watchStatusOptions = listOf(
                WatchStatus.UNWATCHED to "未視聴",
                WatchStatus.WATCHING to "視聴中",
                WatchStatus.COMPLETED to "視聴済",
                WatchStatus.DROPPED to "中止"
            )

            watchStatusOptions.forEach { (status, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedStatus == status,
                            onClick = { if (enabled) onStatusSelected(status) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedStatus == status,
                        onClick = null,
                        enabled = enabled
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                }
            }
        }
    }
}
