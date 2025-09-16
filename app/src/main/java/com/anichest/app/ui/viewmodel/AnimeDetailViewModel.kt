package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * アニメ詳細・編集画面のViewModel
 *
 * 特定のアニメ作品の詳細情報表示、編集、削除機能を提供します。
 * 視聴状況の更新も行います。
 *
 * @param animeRepository アニメデータアクセス用Repository
 * @param animeStatusRepository アニメ視聴状況データアクセス用Repository
 * @see AnimeRepository
 * @see AnimeStatusRepository
 */
@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val animeStatusRepository: AnimeStatusRepository
) : ViewModel() {

    /**
     * 表示中のアニメ作品情報
     */
    private val _anime = MutableStateFlow<Anime?>(null)
    val anime: StateFlow<Anime?> = _anime.asStateFlow()

    /**
     * アニメの視聴状況情報
     */
    private val _animeStatus = MutableStateFlow<AnimeStatus?>(null)
    val animeStatus: StateFlow<AnimeStatus?> = _animeStatus.asStateFlow()

    /**
     * 編集モードの状態
     * trueの場合、アニメ情報の編集が可能な状態
     */
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    /**
     * ローディング状態
     * データ取得中や処理中の表示制御に使用
     */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * エラーメッセージ
     * 処理失敗時のメッセージを保持
     */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 削除完了状態
     * アニメが削除された際にtrueになる
     */
    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    /**
     * 指定されたIDのアニメ情報を読み込み
     *
     * アニメ基本情報と視聴状況を同時に取得します。
     *
     * @param animeId 読み込み対象のアニメID
     */
    fun loadAnime(animeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _isDeleted.value = false // 削除状態をリセット

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

    /**
     * アニメの基本情報と視聴状況を原子的に更新
     *
     * アニメの基本情報と視聴状況を同時に更新し、データの一貫性を保証します。
     * データベーストランザクション内で両方の更新が実行され、
     * 真の原子性を提供します（両方成功または両方失敗）。
     *
     * @param title アニメタイトル
     * @param totalEpisodes 全話数
     * @param genre ジャンル
     * @param year 放送年
     * @param description 作品説明
     * @param status 新しい視聴状況
     * @param rating 評価（1-10）
     * @param review レビューテキスト
     * @param watchedEpisodes 視聴済み話数
     */
    fun updateAnimeAndStatus(
        title: String,
        totalEpisodes: Int,
        genre: String,
        year: Int,
        description: String,
        status: WatchStatus,
        rating: Int,
        review: String,
        watchedEpisodes: Int
    ) {
        val currentAnime = _anime.value ?: return

        viewModelScope.launch {
            try {
                // アニメ基本情報の更新データを準備
                val updatedAnime = currentAnime.copy(
                    title = title,
                    totalEpisodes = totalEpisodes,
                    genre = genre,
                    year = year,
                    description = description
                )

                // 視聴状況の更新データを準備
                val currentStatus = _animeStatus.value

                val newStatus = currentStatus?.copy(
                    status = status,
                    rating = rating,
                    review = review,
                    watchedEpisodes = watchedEpisodes
                )
                    ?: AnimeStatus(
                        animeId = currentAnime.id,
                        status = status,
                        rating = rating,
                        review = review,
                        watchedEpisodes = watchedEpisodes
                    )

                // 原子的更新: データベーストランザクション内で両方の操作を実行
                animeRepository.updateAnimeAndStatus(updatedAnime, newStatus)

                // 成功時のみ状態を更新
                _anime.value = updatedAnime
                _animeStatus.value = newStatus
                _isEditing.value = false

            } catch (e: Exception) {
                _error.value = "アニメ情報の更新に失敗しました"
            }
        }
    }

    /**
     * アニメを削除
     *
     * アニメ作品をデータベースから完全に削除します。
     * 成功時はisDeletedがtrueになります。
     */
    fun deleteAnime() {
        val currentAnime = _anime.value ?: return

        viewModelScope.launch {
            try {
                animeRepository.deleteAnime(currentAnime)
                _isDeleted.value = true
            } catch (e: Exception) {
                _error.value = "アニメの削除に失敗しました"
            }
        }
    }

    /**
     * 編集モードの切り替え
     *
     * @param editing true: 編集モード、false: 表示モード
     */
    fun setEditMode(editing: Boolean) {
        _isEditing.value = editing
    }

    /**
     * エラーメッセージをクリア
     * UI側でエラー表示を消去する際に使用
     */
    fun clearError() {
        _error.value = null
    }
}
