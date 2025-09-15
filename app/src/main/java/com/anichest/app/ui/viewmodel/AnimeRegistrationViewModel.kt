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
 * アニメ登録画面のViewModel
 *
 * 新しいアニメ作品の登録フォームの状態管理と、
 * データベースへの保存処理を担当します。
 *
 * @param animeRepository アニメデータアクセス用Repository
 * @param animeStatusRepository アニメ視聴状況データアクセス用Repository
 * @see AnimeRepository
 * @see AnimeStatusRepository
 * @see Anime
 * @see AnimeStatus
 */
@HiltViewModel
class AnimeRegistrationViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val animeStatusRepository: AnimeStatusRepository
) : ViewModel() {

    /**
     * アニメタイトルの入力状態
     */
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    /**
     * 全話数の入力状態
     */
    private val _totalEpisodes = MutableStateFlow("")
    val totalEpisodes: StateFlow<String> = _totalEpisodes.asStateFlow()

    /**
     * ジャンルの入力状態
     */
    private val _genre = MutableStateFlow("")
    val genre: StateFlow<String> = _genre.asStateFlow()

    /**
     * 放送年の入力状態
     */
    private val _year = MutableStateFlow("")
    val year: StateFlow<String> = _year.asStateFlow()

    /**
     * 作品説明の入力状態
     */
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()


    /**
     * 初期視聴状況の選択状態
     */
    private val _initialWatchStatus = MutableStateFlow(WatchStatus.UNWATCHED)
    val initialWatchStatus: StateFlow<WatchStatus> = _initialWatchStatus.asStateFlow()

    /**
     * 初期評価の選択状態（0-5、0は未評価）
     */
    private val _initialRating = MutableStateFlow(0)
    val initialRating: StateFlow<Int> = _initialRating.asStateFlow()

    /**
     * 初期レビューの入力状態
     */
    private val _initialReview = MutableStateFlow("")
    val initialReview: StateFlow<String> = _initialReview.asStateFlow()

    /**
     * 初期視聴済み話数の入力状態
     */
    private val _initialWatchedEpisodes = MutableStateFlow("")
    val initialWatchedEpisodes: StateFlow<String> = _initialWatchedEpisodes.asStateFlow()

    /**
     * 登録処理中フラグ
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * エラーメッセージ
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 登録完了フラグ
     */
    private val _isRegistrationComplete = MutableStateFlow(false)
    val isRegistrationComplete: StateFlow<Boolean> = _isRegistrationComplete.asStateFlow()

    /**
     * アニメタイトルを更新
     *
     * @param newTitle 新しいタイトル
     */
    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        clearError()
    }

    /**
     * 全話数を更新
     *
     * @param newTotalEpisodes 新しい全話数
     */
    fun updateTotalEpisodes(newTotalEpisodes: String) {
        _totalEpisodes.value = newTotalEpisodes
        clearError()
    }

    /**
     * ジャンルを更新
     *
     * @param newGenre 新しいジャンル
     */
    fun updateGenre(newGenre: String) {
        _genre.value = newGenre
        clearError()
    }

    /**
     * 放送年を更新
     *
     * @param newYear 新しい放送年
     */
    fun updateYear(newYear: String) {
        _year.value = newYear
        clearError()
    }

    /**
     * 作品説明を更新
     *
     * @param newDescription 新しい作品説明
     */
    fun updateDescription(newDescription: String) {
        _description.value = newDescription
        clearError()
    }


    /**
     * 初期視聴状況を更新
     *
     * @param newStatus 新しい視聴状況
     */
    fun updateInitialWatchStatus(newStatus: WatchStatus) {
        _initialWatchStatus.value = newStatus
        clearError()
    }

    /**
     * 初期評価を更新
     *
     * @param newRating 新しい評価（0-5）
     */
    fun updateInitialRating(newRating: Int) {
        _initialRating.value = newRating
        clearError()
    }

    /**
     * 初期レビューを更新
     *
     * @param newReview 新しいレビュー
     */
    fun updateInitialReview(newReview: String) {
        _initialReview.value = newReview
        clearError()
    }

    /**
     * 初期視聴済み話数を更新
     *
     * @param newWatchedEpisodes 新しい視聴済み話数
     */
    fun updateInitialWatchedEpisodes(newWatchedEpisodes: String) {
        _initialWatchedEpisodes.value = newWatchedEpisodes
        clearError()
    }

    /**
     * エラーメッセージをクリア
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * フォームの入力値をバリデーション
     *
     * @return バリデーション結果（true: 有効, false: 無効）
     */
    private fun validateForm(): Boolean {
        return when {
            _title.value.isBlank() -> {
                _errorMessage.value = "タイトルを入力してください"
                false
            }

            _totalEpisodes.value.isNotBlank() && _totalEpisodes.value.toIntOrNull() == null -> {
                _errorMessage.value = "話数は数値で入力してください"
                false
            }

            _totalEpisodes.value.isNotBlank() && _totalEpisodes.value.toInt() < 0 -> {
                _errorMessage.value = "話数は0以上で入力してください"
                false
            }

            _year.value.isNotBlank() && _year.value.toIntOrNull() == null -> {
                _errorMessage.value = "放送年は数値で入力してください"
                false
            }

            _year.value.isNotBlank() && _year.value.toInt() < 1900 -> {
                _errorMessage.value = "放送年は1900年以降で入力してください"
                false
            }

            _initialWatchedEpisodes.value.isNotBlank() && _initialWatchedEpisodes.value.toIntOrNull() == null -> {
                _errorMessage.value = "視聴済み話数は数値で入力してください"
                false
            }

            _initialWatchedEpisodes.value.isNotBlank() && _initialWatchedEpisodes.value.toInt() < 0 -> {
                _errorMessage.value = "視聴済み話数は0以上で入力してください"
                false
            }

            _initialWatchedEpisodes.value.isNotBlank() && _totalEpisodes.value.isNotBlank() &&
                    _initialWatchedEpisodes.value.toInt() > _totalEpisodes.value.toInt() -> {
                _errorMessage.value = "視聴済み話数は全話数以下で入力してください"
                false
            }

            else -> true
        }
    }

    /**
     * アニメ情報を登録
     *
     * フォームのバリデーションを行い、有効な場合は
     * データベースにアニメ情報と初期視聴状況を保存します。
     */
    fun registerAnime() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // アニメ基本情報を作成
                val anime = Anime(
                    title = _title.value.trim(),
                    totalEpisodes = _totalEpisodes.value.toIntOrNull() ?: 0,
                    genre = _genre.value.trim(),
                    year = _year.value.toIntOrNull() ?: 0,
                    description = _description.value.trim()
                )

                // アニメを登録してIDを取得
                val animeId = animeRepository.insertAnime(anime)

                // 初期視聴状況を作成して登録
                val animeStatus = AnimeStatus(
                    animeId = animeId,
                    status = _initialWatchStatus.value,
                    rating = _initialRating.value,
                    review = _initialReview.value.trim(),
                    watchedEpisodes = _initialWatchedEpisodes.value.toIntOrNull() ?: 0
                )
                animeStatusRepository.insertOrUpdateStatus(animeStatus)

                _isRegistrationComplete.value = true
            } catch (e: Exception) {
                _errorMessage.value = "登録に失敗しました: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * フォームをリセット
     * 全ての入力値と状態を初期化します
     */
    fun resetForm() {
        _title.value = ""
        _totalEpisodes.value = ""
        _genre.value = ""
        _year.value = ""
        _description.value = ""
        _initialWatchStatus.value = WatchStatus.UNWATCHED
        _initialRating.value = 0
        _initialReview.value = ""
        _initialWatchedEpisodes.value = ""
        _isLoading.value = false
        _errorMessage.value = null
        _isRegistrationComplete.value = false
    }
}
