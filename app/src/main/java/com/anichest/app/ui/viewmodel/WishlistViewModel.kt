package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.repository.WishlistRepository
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.AnimeWithWishlistAndStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ウィッシュリスト画面のViewModel
 */
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    val selectedPriority: StateFlow<Priority?> = _selectedPriority.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun mapToAnimeWithWishlist(item: AnimeWithWishlistAndStatus): AnimeWithWishlist {
        return AnimeWithWishlist(
            anime = item.anime,
            wishlistItem = item.wishlistItem
        )
    }

    // 未視聴ウィッシュリストと優先度フィルター
    val wishlistItems = combine(
        wishlistRepository.getUnwatchedWishlistWithAnime(),
        selectedPriority
    ) { allItems, priority ->
        _isLoading.value = false

        if (priority != null) {
            allItems.filter { it.wishlistItem?.priority == priority }
        } else {
            allItems
        }
    }.map { items ->
        items.map { mapToAnimeWithWishlist(it) }
    }

    val wishlistCount = wishlistRepository.getWishlistCount()

    fun setPriorityFilter(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun clearPriorityFilter() {
        _selectedPriority.value = null
    }

    fun updatePriority(animeId: Long, priority: Priority) {
        viewModelScope.launch {
            try {
                val item = wishlistRepository.getWishlistItemByAnimeId(animeId)
                if (item != null) {
                    val updatedItem = item.copy(priority = priority)
                    wishlistRepository.updateWishlistItem(updatedItem)
                }
            } catch (e: Exception) {
                // エラーハンドリング
            }
        }
    }

    fun removeFromWishlist(animeId: Long) {
        viewModelScope.launch {
            try {
                wishlistRepository.deleteWishlistItemByAnimeId(animeId)
            } catch (e: Exception) {
                // エラーハンドリング
            }
        }
    }
}
