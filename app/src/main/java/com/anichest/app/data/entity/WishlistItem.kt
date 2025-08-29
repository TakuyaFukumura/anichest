package com.anichest.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 優先度を表すenum
 */
enum class Priority {
    LOW,      // 低
    MEDIUM,   // 中
    HIGH      // 高
}

/**
 * 視聴予定リスト（ウィッシュリスト）を管理するエンティティクラス
 *
 * @property id 主キー
 * @property animeId 対象アニメのID（外部キー）
 * @property priority 優先度（3段階）
 * @property addedAt 追加日時（エポック秒）
 * @property notes メモ・備考
 */
@Entity(
    tableName = "wishlist",
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
data class WishlistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val animeId: Long,
    val priority: Priority = Priority.MEDIUM,
    val addedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
