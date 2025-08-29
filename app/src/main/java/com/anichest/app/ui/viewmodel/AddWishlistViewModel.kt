package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WishlistItem
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ウィッシュリスト新規追加画面のUIState
 */
data class AddWishlistUiState(
    val title: String = "",
    val totalEpisodes: String = "",
    val genre: String = "",
    val year: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

/**
 * ウィッシュリスト新規追加のViewModel
 */
class AddWishlistViewModel(
    private val animeRepository: AnimeRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddWishlistUiState())
    val uiState: StateFlow<AddWishlistUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateTotalEpisodes(episodes: String) {
        // 数字のみ許可
        if (episodes.isEmpty() || episodes.matches(Regex("\\d*"))) {
            _uiState.value = _uiState.value.copy(totalEpisodes = episodes)
        }
    }

    fun updateGenre(genre: String) {
        _uiState.value = _uiState.value.copy(genre = genre)
    }

    fun updateYear(year: String) {
        // 数字のみ許可（4桁まで）
        if (year.isEmpty() || year.matches(Regex("\\d{0,4}"))) {
            _uiState.value = _uiState.value.copy(year = year)
        }
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * ウィッシュリストアイテムを保存
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
                    animeId = animeId,
                    priority = currentState.priority,
                    notes = currentState.notes.trim()
                )

                // ウィッシュリストに追加
                wishlistRepository.insertWishlistItem(wishlistItem)

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
}

/**
 * AddWishlistViewModelのFactory
 */
class AddWishlistViewModelFactory(
    private val animeRepository: AnimeRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWishlistViewModel::class.java)) {
            return AddWishlistViewModel(animeRepository, wishlistRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
