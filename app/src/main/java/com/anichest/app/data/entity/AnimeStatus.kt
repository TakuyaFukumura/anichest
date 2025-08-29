package com.anichest.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 視聴状況を表すenum
 */
enum class WatchStatus {
    UNWATCHED,    // 未視聴
    WATCHING,     // 視聴中  
    COMPLETED,    // 視聴済
    DROPPED       // 中止
}

/**
 * アニメの視聴状況と評価を管理するエンティティクラス
 *
 * @property id 主キー
 * @property animeId 対象アニメのID（外部キー）
 * @property status 視聴状況
 * @property rating 5段階評価（1-5、未評価は0）
 * @property review 感想・レビュー
 * @property watchedEpisodes 視聴済み話数
 * @property startDate 視聴開始日（YYYY-MM-DD形式）
 * @property finishDate 視聴完了日（YYYY-MM-DD形式）
 * @property updatedAt 最終更新日時（エポック秒）
 */
@Entity(
    tableName = "anime_status",
    foreignKeys = [
        ForeignKey(
            entity = Anime::class,
            parentColumns = ["id"],
            childColumns = ["animeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["animeId"], unique = true)]
)
data class AnimeStatus(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val animeId: Int,
    val status: WatchStatus = WatchStatus.UNWATCHED,
    val rating: Int = 0, // 0: 未評価, 1-5: 評価
    val review: String = "",
    val watchedEpisodes: Int = 0,
    val startDate: String = "",
    val finishDate: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
