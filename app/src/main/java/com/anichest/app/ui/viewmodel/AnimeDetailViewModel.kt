package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * アニメ詳細・編集画面のViewModel
 */
class AnimeDetailViewModel(
    private val animeRepository: AnimeRepository,
    private val animeStatusRepository: AnimeStatusRepository
) : ViewModel() {

    private val _anime = MutableStateFlow<Anime?>(null)
    val anime: StateFlow<Anime?> = _anime.asStateFlow()

    private val _animeStatus = MutableStateFlow<AnimeStatus?>(null)
    val animeStatus: StateFlow<AnimeStatus?> = _animeStatus.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadAnime(animeId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val anime = animeRepository.getAnimeById(animeId)
                _anime.value = anime
                
                val status = animeStatusRepository.getStatusByAnimeId(animeId)
                _animeStatus.value = status
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "アニメ情報の読み込みに失敗しました"
                _isLoading.value = false
            }
        }
    }

    fun updateAnimeStatus(
        status: WatchStatus,
        rating: Int,
        review: String,
        watchedEpisodes: Int
    ) {
        val currentAnime = _anime.value ?: return
        
        viewModelScope.launch {
            try {
                val currentStatus = _animeStatus.value
                
                val newStatus = if (currentStatus != null) {
                    currentStatus.copy(
                        status = status,
                        rating = rating,
                        review = review,
                        watchedEpisodes = watchedEpisodes,
                        updatedAt = System.currentTimeMillis(),
                        finishDate = if (status == WatchStatus.COMPLETED) {
                            java.time.LocalDate.now().toString()
                        } else currentStatus.finishDate
                    )
                } else {
                    AnimeStatus(
                        animeId = currentAnime.id,
                        status = status,
                        rating = rating,
                        review = review,
                        watchedEpisodes = watchedEpisodes,
                        startDate = if (status == WatchStatus.WATCHING) {
                            java.time.LocalDate.now().toString()
                        } else "",
                        finishDate = if (status == WatchStatus.COMPLETED) {
                            java.time.LocalDate.now().toString()
                        } else ""
                    )
                }
                
                animeStatusRepository.insertOrUpdateStatus(newStatus)
                _animeStatus.value = newStatus
                
            } catch (e: Exception) {
                _error.value = "視聴状況の更新に失敗しました"
            }
        }
    }

    fun updateAnime(
        title: String,
        totalEpisodes: Int,
        genre: String,
        year: Int,
        description: String
    ) {
        val currentAnime = _anime.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedAnime = currentAnime.copy(
                    title = title,
                    totalEpisodes = totalEpisodes,
                    genre = genre,
                    year = year,
                    description = description
                )
                
                animeRepository.updateAnime(updatedAnime)
                _anime.value = updatedAnime
                _isEditing.value = false
                
            } catch (e: Exception) {
                _error.value = "アニメ情報の更新に失敗しました"
            }
        }
    }

    fun deleteAnime() {
        val currentAnime = _anime.value ?: return
        
        viewModelScope.launch {
            try {
                animeRepository.deleteAnime(currentAnime)
            } catch (e: Exception) {
                _error.value = "アニメの削除に失敗しました"
            }
        }
    }

    fun setEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    fun clearError() {
        _error.value = null
    }
}

class AnimeDetailViewModelFactory(
    private val animeRepository: AnimeRepository,
    private val animeStatusRepository: AnimeStatusRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimeDetailViewModel(animeRepository, animeStatusRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}