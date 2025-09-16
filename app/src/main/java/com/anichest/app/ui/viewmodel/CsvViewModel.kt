package com.anichest.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.util.CsvUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CSV出力・入力機能を管理するViewModel
 */
@HiltViewModel
class CsvViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CsvUiState())
    val uiState: StateFlow<CsvUiState> = _uiState.asStateFlow()

    /**
     * アニメデータをCSV形式で出力
     */
    suspend fun exportToCsv(): String {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        return try {
            val csvData = animeRepository.exportToCsv()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                exportResult = CsvUtils.generateCsvFileName()
            )
            csvData
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "エクスポートに失敗しました: ${e.message}"
            )
            throw e
        }
    }

    /**
     * CSVファイルからアニメデータをインポート
     *
     * @param context アプリケーションコンテキスト
     * @param uri CSVファイルのURI
     */
    fun importFromCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = animeRepository.importFromCsv(context, uri)

                val message = buildString {
                    if (result.successCount > 0) {
                        append("${result.successCount}件のアニメを登録しました")
                    }
                    if (result.skipCount > 0) {
                        if (result.successCount > 0) append("\n")
                        append("${result.skipCount}件のアニメがスキップされました（既存タイトル）")
                    }
                    if (result.errors.isNotEmpty()) {
                        if (result.successCount > 0 || result.skipCount > 0) append("\n")
                        append("エラー:\n${result.errors.joinToString("\n")}")
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    importResult = ImportResultState(
                        successCount = result.successCount,
                        skipCount = result.skipCount,
                        message = message,
                        hasErrors = result.errors.isNotEmpty()
                    )
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "インポートに失敗しました: ${e.message}"
                )
            }
        }
    }

    /**
     * エラーメッセージをクリア
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 結果をクリア
     */
    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            exportResult = null,
            importResult = null
        )
    }
}

/**
 * CSV機能のUI状態を表すデータクラス
 *
 * @property isLoading 処理中フラグ
 * @property error エラーメッセージ
 * @property exportResult エクスポート結果（ファイル名）
 * @property importResult インポート結果
 */
data class CsvUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val exportResult: String? = null,
    val importResult: ImportResultState? = null
)

/**
 * インポート結果の状態を表すデータクラス
 *
 * @property successCount 成功件数
 * @property skipCount スキップ件数
 * @property message 結果メッセージ
 * @property hasErrors エラーがあるかどうか
 */
data class ImportResultState(
    val successCount: Int,
    val skipCount: Int,
    val message: String,
    val hasErrors: Boolean
)
