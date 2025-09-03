package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.AnimeWithWishlist
import com.anichest.app.data.entity.AnimeWithWishlistAndStatus
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.data.repository.WishlistRepository
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
 * 
 * 視聴予定アニメの一覧表示、優先度によるフィルタリング、
 * ウィッシュリストアイテムの管理機能を提供します。
 * 
 * @param wishlistRepository ウィッシュリストデータアクセス用Repository
 * @see WishlistRepository
 * @see Priority
 * @see AnimeWithWishlist
 */
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    /**
     * 選択された優先度フィルター
     * nullの場合は全優先度を表示
     */
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    val selectedPriority: StateFlow<Priority?> = _selectedPriority.asStateFlow()

    /**
     * ローディング状態
     * データ取得中の表示制御に使用
     */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * AnimeWithWishlistAndStatusをAnimeWithWishlistに変換
     * 
     * 未視聴アイテムのみを対象とし、データ整合性をチェックします。
     * 
     * @param item 変換元のアニメ・ウィッシュリスト・ステータス結合データ
     * @return 変換後のアニメ・ウィッシュリスト結合データ
     * @throws IllegalArgumentException 視聴状況が未視聴でない場合
     */
    private fun mapToAnimeWithWishlist(item: AnimeWithWishlistAndStatus): AnimeWithWishlist {
        require(item.status?.status == WatchStatus.UNWATCHED || item.status == null) {
            "statusが未視聴でもnullでもないアイテムが含まれています"
        }
        return AnimeWithWishlist(
            anime = item.anime,
            wishlistItem = item.wishlistItem
        )
    }

    /**
     * 未視聴ウィッシュリストと優先度フィルター結果
     * 
     * 未視聴のアニメのみを表示し、選択された優先度でフィルタリングします。
     * データ取得完了時にローディング状態を更新します。
     */
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

    /**
     * ウィッシュリストアイテムの総数
     * ホーム画面やヘッダーの統計表示に使用
     */
    val wishlistCount = wishlistRepository.getWishlistCount()

    /**
     * 優先度フィルターを設定
     * 
     * @param priority 設定する優先度（nullの場合はフィルターなし）
     */
    fun setPriorityFilter(priority: Priority?) {
        _selectedPriority.value = priority
    }

    /**
     * 優先度フィルターをクリア
     * 全ての優先度のアイテムを表示する状態に戻します
     */
    fun clearPriorityFilter() {
        _selectedPriority.value = null
    }

    /**
     * ウィッシュリストアイテムの優先度を更新
     * 
     * @param animeId 対象のアニメID
     * @param priority 新しい優先度
     */
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
                // TODO: エラー状態の管理を追加することを検討
            }
        }
    }

    /**
     * ウィッシュリストからアイテムを削除
     * 
     * @param animeId 削除対象のアニメID
     */
    fun removeFromWishlist(animeId: Long) {
        viewModelScope.launch {
            try {
                wishlistRepository.deleteWishlistItemByAnimeId(animeId)
            } catch (e: Exception) {
                // エラーハンドリング
                // TODO: エラー状態の管理を追加することを検討
            }
        }
    }
}
