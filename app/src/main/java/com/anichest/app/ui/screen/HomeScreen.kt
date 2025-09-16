package com.anichest.app.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.data.preferences.ThemeMode
import com.anichest.app.ui.viewmodel.AnimeListViewModel
import com.anichest.app.ui.viewmodel.CsvViewModel
import com.anichest.app.ui.viewmodel.ThemeViewModel
import com.anichest.app.util.CsvUtils
import kotlinx.coroutines.launch

/**
 * ホーム画面
 *
 * アニメの統計情報を表示するアプリのメイン画面です。
 * 視聴中・完了のアニメ数の統計と、
 * 各カテゴリへのナビゲーション機能、およびテーマ切り替え機能、
 * CSV出力・入力機能を提供します。
 *
 * @param viewModel アニメリスト情報を提供するViewModel
 * @param onNavigateToAnimeList アニメリスト画面への遷移コールバック
 * @param onNavigateToAnimeRegistration アニメ登録画面への遷移コールバック
 * @param themeViewModel テーマ設定を管理するViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AnimeListViewModel,
    onNavigateToAnimeList: (WatchStatus?) -> Unit = {},
    onNavigateToAnimeRegistration: () -> Unit = {},
    themeViewModel: ThemeViewModel
) {
    val animeList by viewModel.animeList.collectAsState(initial = emptyList())
    val watchingCount by viewModel.watchingCount.collectAsState(initial = 0)
    val completedCount by viewModel.completedCount.collectAsState(initial = 0)
    val unwatchedCount by viewModel.unwatchedCount.collectAsState(initial = 0)
    val droppedCount by viewModel.droppedCount.collectAsState(initial = 0)
    val isLoading by viewModel.isLoading.collectAsState()
    val themePreferences by themeViewModel.themePreferences.collectAsState()

    // CSV機能用のViewModel
    val csvViewModel: CsvViewModel = hiltViewModel()
    val csvUiState by csvViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    // ファイルエクスポート用ランチャー
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            // コルーチンスコープでエクスポートを実行
            coroutineScope.launch {
                exportCsvToFile(context, csvViewModel, it)
            }
        }
    }

    // ファイルインポート用ランチャー
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            csvViewModel.importFromCsv(context, it)
        }
    }

    // エラーメッセージの表示
    LaunchedEffect(csvUiState.error) {
        csvUiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            csvViewModel.clearError()
        }
    }

    // インポート結果の表示
    LaunchedEffect(csvUiState.importResult) {
        csvUiState.importResult?.let { result ->
            snackbarHostState.showSnackbar(result.message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Anichest",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { themeViewModel.toggleThemeMode() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "テーマ切り替え (現在: ${
                                getThemeModeDescription(
                                    themePreferences.themeMode
                                )
                            })",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("CSVエクスポート") },
                            onClick = {
                                showMenu = false
                                exportLauncher.launch(CsvUtils.generateCsvFileName())
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("CSVインポート") },
                            onClick = {
                                showMenu = false
                                importLauncher.launch("text/csv")
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAnimeRegistration,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "アニメを追加"
                )
            }
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
        ) {
            if (isLoading || csvUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 統計セクション
                StatsSection(
                    watchingCount = watchingCount,
                    completedCount = completedCount,
                    unwatchedCount = unwatchedCount,
                    droppedCount = droppedCount,
                    totalCount = animeList.size,
                    onNavigateToAnimeList = onNavigateToAnimeList
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    watchingCount: Int,
    completedCount: Int,
    unwatchedCount: Int,
    droppedCount: Int,
    totalCount: Int,
    onNavigateToAnimeList: (WatchStatus?) -> Unit
) {
    Column {
        // 第一行：視聴中・完了
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
                icon = Icons.Filled.CheckCircle,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { onNavigateToAnimeList(WatchStatus.COMPLETED) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 第二行：未視聴・中止
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "未視聴",
                count = unwatchedCount,
                icon = Icons.Filled.Search,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { onNavigateToAnimeList(WatchStatus.UNWATCHED) }
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "中止",
                count = droppedCount,
                icon = Icons.Filled.Delete,
                color = MaterialTheme.colorScheme.error,
                onClick = { onNavigateToAnimeList(WatchStatus.DROPPED) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 第三行：合計
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "合計",
                count = totalCount,
                icon = Icons.AutoMirrored.Filled.List,
                color = MaterialTheme.colorScheme.outline,
                onClick = { onNavigateToAnimeList(null) }
            )

            // 空のスペースを埋めるための透明なCard
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                )
            ) {
                // 空のコンテンツ
                Spacer(modifier = Modifier.height(80.dp))
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getThemeModeDescription(themeMode: ThemeMode): String {
    return when (themeMode) {
        ThemeMode.LIGHT -> "ライトモード"
        ThemeMode.DARK -> "ダークモード"
        ThemeMode.SYSTEM -> "システム設定"
    }
}

/**
 * CSVファイルをエクスポートする関数
 *
 * @param context アプリケーションコンテキスト
 * @param csvViewModel CSV機能用ViewModel
 * @param uri 保存先のURI
 */
private suspend fun exportCsvToFile(context: Context, csvViewModel: CsvViewModel, uri: Uri) {
    try {
        val csvData = csvViewModel.exportToCsv()
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.writer().use { writer ->
                writer.write(csvData)
            }
        }
    } catch (e: Exception) {
        // エラーはViewModelで処理される
    }
}
