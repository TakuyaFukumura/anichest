package com.anichest.app.di

import android.content.Context
import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.dao.StringDao
import com.anichest.app.data.dao.WishlistDao
import com.anichest.app.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * データベース関連の依存関係を提供するHiltモジュール
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, applicationScope)
    }

    @Provides
    fun provideAnimeDao(database: AppDatabase): AnimeDao {
        return database.animeDao()
    }

    @Provides
    fun provideAnimeStatusDao(database: AppDatabase): AnimeStatusDao {
        return database.animeStatusDao()
    }

    @Provides
    fun provideWishlistDao(database: AppDatabase): WishlistDao {
        return database.wishlistDao()
    }

    @Provides
    fun provideStringDao(database: AppDatabase): StringDao {
        return database.stringDao()
    }
}