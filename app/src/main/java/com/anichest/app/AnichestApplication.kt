package com.anichest.app

import android.app.Application
import com.anichest.app.data.database.AppDatabase
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import com.anichest.app.data.repository.StringRepository
import com.anichest.app.data.repository.WishlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * アニメ視聴管理アプリのメインApplicationクラス
 */
class AnichestApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy {
        AppDatabase.getDatabase(this, applicationScope)
    }

    // 各Repositoryのインスタンス
    val animeRepository by lazy {
        AnimeRepository(database.animeDao())
    }

    val animeStatusRepository by lazy {
        AnimeStatusRepository(database.animeStatusDao())
    }

    val wishlistRepository by lazy {
        WishlistRepository(database.wishlistDao())
    }

    // 後方互換性のため残す
    val stringRepository by lazy {
        StringRepository(database.stringDao())
    }
}
