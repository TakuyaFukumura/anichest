package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * アニメリスト画面のViewModel
 */
@HiltViewModel
class AnimeListViewModel @Inject constructor(
    animeRepository: AnimeRepository,
    animeStatusRepository: AnimeStatusRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow<WatchStatus?>(null)
    val selectedFilter: StateFlow<WatchStatus?> = _selectedFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 全アニメリストと検索・フィルター
    val animeList = combine(
        animeRepository.getAllAnimeWithStatus(),
        searchQuery,
        selectedFilter
    ) { allAnime, query, filter ->
        _isLoading.value = false

        var filteredList = allAnime

        // 検索フィルター
        if (query.isNotBlank()) {
            filteredList = filteredList.filter { animeWithStatus ->
                animeWithStatus.anime.title.contains(query, ignoreCase = true)
            }
        }

        // 視聴状況フィルター
        if (filter != null) {
            filteredList = filteredList.filter { animeWithStatus ->
                animeWithStatus.status?.status == filter ||
                        (animeWithStatus.status == null && filter == WatchStatus.UNWATCHED)
            }
        }

        filteredList
    }

    // 統計情報
    val watchingCount = animeStatusRepository.getWatchingCount()
    val completedCount = animeStatusRepository.getCompletedCount()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(status: WatchStatus?) {
        _selectedFilter.value = status
    }

    fun clearFilter() {
        _selectedFilter.value = null
    }
}