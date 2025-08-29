package com.anichest.app.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * アニメ作品と視聴状況を結合したデータクラス
 */
data class AnimeWithStatus(
    @Embedded val anime: Anime,
    @Relation(
        parentColumn = "id",
        entityColumn = "animeId"
    )
    val status: AnimeStatus?
)

/**
 * アニメ作品とウィッシュリスト情報を結合したデータクラス
 */
data class AnimeWithWishlist(
    @Embedded val anime: Anime,
    @Relation(
        parentColumn = "id",
        entityColumn = "animeId"
    )
    val wishlistItem: WishlistItem?
)
