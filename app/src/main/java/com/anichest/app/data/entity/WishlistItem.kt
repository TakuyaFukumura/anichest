package com.anichest.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 視聴予定リスト（ウィッシュリスト）を管理するエンティティクラス
 *
 * @property id 主キー
 * @property animeId 対象アニメのID（外部キー）
 * @property addedAt 追加日時（エポック秒）
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
    val addedAt: Long = System.currentTimeMillis()
)
