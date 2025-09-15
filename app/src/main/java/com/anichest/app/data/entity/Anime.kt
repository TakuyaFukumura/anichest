package com.anichest.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * アニメ作品の基本情報を表すエンティティクラス
 *
 * @property id 主キー。自動生成される一意の識別子
 * @property title アニメタイトル
 * @property totalEpisodes 全話数（不明な場合は0）
 * @property genre ジャンル（カンマ区切りで複数指定可能）
 * @property year 放送年
 * @property description 作品の説明・あらすじ
 */
@Entity(tableName = "anime")
data class Anime(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val totalEpisodes: Int = 0,
    val genre: String = "",
    val year: Int = 0,
    val description: String = ""
)
