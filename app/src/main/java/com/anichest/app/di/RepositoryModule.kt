package com.anichest.app.di

import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.dao.StringDao
import com.anichest.app.data.dao.WishlistDao
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import com.anichest.app.data.repository.StringRepository
import com.anichest.app.data.repository.WishlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * リポジトリ関連の依存関係を提供するHiltモジュール
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAnimeRepository(animeDao: AnimeDao): AnimeRepository {
        return AnimeRepository(animeDao)
    }

    @Provides
    fun provideAnimeStatusRepository(animeStatusDao: AnimeStatusDao): AnimeStatusRepository {
        return AnimeStatusRepository(animeStatusDao)
    }

    @Provides
    fun provideWishlistRepository(wishlistDao: WishlistDao): WishlistRepository {
        return WishlistRepository(wishlistDao)
    }

    @Provides
    fun provideStringRepository(stringDao: StringDao): StringRepository {
        return StringRepository(stringDao)
    }
}