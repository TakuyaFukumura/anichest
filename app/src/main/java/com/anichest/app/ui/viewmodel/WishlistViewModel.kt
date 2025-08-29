package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ウィッシュリスト画面のViewModel
 */
class WishlistViewModel(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    val selectedPriority: StateFlow<Priority?> = _selectedPriority.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ウィッシュリストと優先度フィルター
    val wishlistItems = combine(
        wishlistRepository.getWishlistWithAnime(),
        selectedPriority
    ) { allItems, priority ->
        _isLoading.value = false

        if (priority != null) {
            allItems.filter { it.wishlistItem?.priority == priority }
        } else {
            allItems
        }
    }

    val wishlistCount = wishlistRepository.getWishlistCount()

    fun setPriorityFilter(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun clearPriorityFilter() {
        _selectedPriority.value = null
    }

    fun updatePriority(animeId: Int, priority: Priority) {
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

    fun removeFromWishlist(animeId: Int) {
        viewModelScope.launch {
            try {
                wishlistRepository.deleteWishlistItemByAnimeId(animeId)
            } catch (e: Exception) {
                // エラーハンドリング
            }
        }
    }
}

class WishlistViewModelFactory(
    private val wishlistRepository: WishlistRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WishlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WishlistViewModel(wishlistRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
