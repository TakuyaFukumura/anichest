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
 * 
 * アニメ一覧の表示、検索、フィルタリング機能を提供します。
 * 視聴状況による絞り込みや統計情報の表示も担当します。
 * 
 * @param animeRepository アニメデータアクセス用Repository
 * @param animeStatusRepository アニメ視聴状況データアクセス用Repository
 * @see AnimeRepository
 * @see AnimeStatusRepository
 * @see WatchStatus
 */
@HiltViewModel
class AnimeListViewModel @Inject constructor(
    animeRepository: AnimeRepository,
    animeStatusRepository: AnimeStatusRepository
) : ViewModel() {

    /**
     * 検索クエリの状態
     * ユーザーが入力した検索文字列を保持します
     */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * 選択されたフィルター状態
     * 視聴状況によるフィルタリングに使用されます
     */
    private val _selectedFilter = MutableStateFlow<WatchStatus?>(null)
    val selectedFilter: StateFlow<WatchStatus?> = _selectedFilter.asStateFlow()

    /**
     * ローディング状態
     * データ取得中の表示制御に使用されます
     */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * 全アニメリストと検索・フィルター結果
     * 
     * 検索クエリと選択されたフィルターに基づいて、
     * アニメリストを動的にフィルタリングします。
     * - 検索：タイトルの部分一致（大文字小文字区別なし）
     * - フィルター：視聴状況による絞り込み
     */
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

    /**
     * 視聴中のアニメ数
     * ホーム画面の統計表示に使用されます
     */
    val watchingCount = animeStatusRepository.getWatchingCount()

    /**
     * 視聴完了のアニメ数
     * ホーム画面の統計表示に使用されます
     */
    val completedCount = animeStatusRepository.getCompletedCount()

    /**
     * 未視聴のアニメ数
     * ホーム画面の統計表示に使用されます
     */
    val unwatchedCount = animeStatusRepository.getUnwatchedCount()

    /**
     * 中止したアニメ数
     * ホーム画面の統計表示に使用されます
     */
    val droppedCount = animeStatusRepository.getDroppedCount()

    /**
     * 検索クエリを更新
     * 
     * @param query 新しい検索クエリ
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 視聴状況フィルターを設定
     * 
     * @param status 設定する視聴状況（nullの場合はフィルターなし）
     */
    fun setFilter(status: WatchStatus?) {
        _selectedFilter.value = status
    }

    /**
     * フィルターをクリア
     * 全てのアニメを表示する状態に戻します
     */
    fun clearFilter() {
        _selectedFilter.value = null
    }
}
