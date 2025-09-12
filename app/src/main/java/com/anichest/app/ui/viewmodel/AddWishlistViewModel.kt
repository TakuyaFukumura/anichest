package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.data.entity.WishlistItem
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import com.anichest.app.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ウィッシュリスト新規追加画面のUIState
 * 
 * 新規アニメ作品とウィッシュリストアイテムの追加に必要な
 * 全ての入力値と状態を管理します。
 * 
 * @property title アニメタイトル
 * @property totalEpisodes 全話数（文字列）
 * @property genre ジャンル
 * @property year 放送年（文字列）
 * @property description 作品説明
 * @property watchStatus 初期視聴状況
 * @property isLoading 保存処理中の状態
 * @property isSaved 保存完了の状態
 * @property error エラーメッセージ
 */
data class AddWishlistUiState(
    val title: String = "",
    val totalEpisodes: String = "",
    val genre: String = "",
    val year: String = "",
    val description: String = "",
    val watchStatus: WatchStatus = WatchStatus.UNWATCHED,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

/**
 * ウィッシュリスト新規追加のViewModel
 * 
 * 新しいアニメ作品の登録とウィッシュリストへの追加を行います。
 * 入力値の検証、データの保存、エラーハンドリングを担当します。
 * 
 * @param animeRepository アニメデータアクセス用Repository
 * @param wishlistRepository ウィッシュリストデータアクセス用Repository
 * @param animeStatusRepository アニメ視聴状況データアクセス用Repository
 * @see AnimeRepository
 * @see WishlistRepository
 * @see AnimeStatusRepository
 */
@HiltViewModel
class AddWishlistViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val wishlistRepository: WishlistRepository,
    private val animeStatusRepository: AnimeStatusRepository
) : ViewModel() {

    /**
     * UI状態の管理
     */
    private val _uiState = MutableStateFlow(AddWishlistUiState())
    val uiState: StateFlow<AddWishlistUiState> = _uiState.asStateFlow()

    /**
     * アニメタイトルを更新
     * 
     * @param title 新しいタイトル
     */
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    /**
     * 全話数を更新
     * 数字のみを許可し、無効な入力は無視されます。
     * 
     * @param episodes 全話数（数字文字列）
     */
    fun updateTotalEpisodes(episodes: String) {
        // 数字のみ許可
        if (episodes.isEmpty() || episodes.matches(Regex("\\d*"))) {
            _uiState.value = _uiState.value.copy(totalEpisodes = episodes)
        }
    }

    /**
     * ジャンルを更新
     * 
     * @param genre 新しいジャンル
     */
    fun updateGenre(genre: String) {
        _uiState.value = _uiState.value.copy(genre = genre)
    }

    /**
     * 放送年を更新
     * 4桁までの数字のみを許可し、無効な入力は無視されます。
     * 
     * @param year 放送年（数字文字列）
     */
    fun updateYear(year: String) {
        // 数字のみ許可（4桁まで）
        if (year.isEmpty() || year.matches(Regex("\\d{0,4}"))) {
            _uiState.value = _uiState.value.copy(year = year)
        }
    }

    /**
     * 作品説明を更新
     * 
     * @param description 新しい作品説明
     */
    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    /**
     * 初期視聴状況を更新
     * 
     * @param watchStatus 新しい視聴状況
     */
    fun updateWatchStatus(watchStatus: WatchStatus) {
        _uiState.value = _uiState.value.copy(watchStatus = watchStatus)
    }

    /**
     * エラーメッセージをクリア
     * UI側でエラー表示を消去する際に使用
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * ウィッシュリストアイテムを保存
     * 
     * 以下の処理を順次実行します：
     * 1. 入力値の検証
     * 2. アニメ作品の作成・保存
     * 3. ウィッシュリストアイテムの作成・保存
     * 4. 初期視聴ステータスの作成・保存
     * 
     * 保存成功時はisSavedがtrueになります。
     */
    fun saveWishlistItem() {
        val currentState = _uiState.value

        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(error = "タイトルを入力してください")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isLoading = true, error = null)

                // アニメ情報を作成
                val anime = Anime(
                    title = currentState.title.trim(),
                    totalEpisodes = currentState.totalEpisodes.toIntOrNull() ?: 0,
                    genre = currentState.genre.trim(),
                    year = currentState.year.toIntOrNull() ?: 0,
                    description = currentState.description.trim()
                )

                // アニメをデータベースに保存
                val animeId = animeRepository.insertAnime(anime)

                // ウィッシュリストアイテムを作成
                val wishlistItem = WishlistItem(
                    animeId = animeId
                )

                // ウィッシュリストに追加
                wishlistRepository.insertWishlistItem(wishlistItem)

                // 視聴ステータスを作成・保存
                val animeStatus = AnimeStatus(
                    animeId = animeId,
                    status = currentState.watchStatus,
                    rating = 0,
                    review = "",
                    watchedEpisodes = 0,
                    startDate = if (currentState.watchStatus == WatchStatus.WATCHING) {
                        LocalDate.now().toString()
                    } else "",
                    finishDate = if (currentState.watchStatus == WatchStatus.COMPLETED) {
                        LocalDate.now().toString()
                    } else ""
                )
                animeStatusRepository.insertOrUpdateStatus(animeStatus)

                _uiState.value = currentState.copy(
                    isLoading = false,
                    isSaved = true
                )

            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "保存に失敗しました: ${e.message}"
                )
            }
        }
    }

    /**
     * 状態を初期化（画面遷移直後などで呼び出し）
     */
    fun reset() {
        _uiState.value = AddWishlistUiState()
    }
}
