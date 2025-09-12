package com.anichest.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.ui.util.WatchStatusUtils
import com.anichest.app.ui.viewmodel.AddWishlistViewModel

/**
 * ウィッシュリスト新規追加画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWishlistScreen(
    viewModel: AddWishlistViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 画面初期化時に状態リセット
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    // 保存成功時の処理
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    // エラー表示
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ウィッシュリストに追加") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        text = "アニメ情報",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // アニメタイトル
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::updateTitle,
                        label = { Text("タイトル") },
                        placeholder = { Text("アニメのタイトルを入力") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 全話数
                    OutlinedTextField(
                        value = uiState.totalEpisodes,
                        onValueChange = viewModel::updateTotalEpisodes,
                        label = { Text("全話数") },
                        placeholder = { Text("例: 12") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    // ジャンル
                    OutlinedTextField(
                        value = uiState.genre,
                        onValueChange = viewModel::updateGenre,
                        label = { Text("ジャンル") },
                        placeholder = { Text("例: SF, アクション") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 放送年
                    OutlinedTextField(
                        value = uiState.year,
                        onValueChange = viewModel::updateYear,
                        label = { Text("放送年") },
                        placeholder = { Text("例: 2024") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    // 説明
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::updateDescription,
                        label = { Text("説明・あらすじ") },
                        placeholder = { Text("作品の説明やあらすじを入力（任意）") },
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
                            value = WatchStatusUtils.getWatchStatusText(uiState.watchStatus),
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
                                        viewModel.updateWatchStatus(status)
                                        watchStatusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 保存ボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = viewModel::saveWishlistItem,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading && uiState.title.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Text("保存")
                    }
                }
            }

            if (uiState.title.isBlank()) {
                Text(
                    text = "※ タイトルは必須項目です",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
