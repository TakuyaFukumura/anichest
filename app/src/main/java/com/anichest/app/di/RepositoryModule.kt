package com.anichest.app.di

import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.repository.AnimeRepository
import com.anichest.app.data.repository.AnimeStatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * リポジトリ関連の依存関係を提供するHiltモジュール
 * 
 * DAOを使用してRepositoryパターンを実装するクラスを提供します。
 * ViewModelとDAOの間の抽象化レイヤーとして機能します。
 * 
 * @see AnimeRepository
 * @see AnimeStatusRepository
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * アニメデータリポジトリを提供
     * アニメ作品の基本情報へのアクセスを抽象化します
     * 
     * @param animeDao アニメデータアクセス用DAO
     * @return AnimeRepository
     */
    @Provides
    fun provideAnimeRepository(animeDao: AnimeDao): AnimeRepository {
        return AnimeRepository(animeDao)
    }

    /**
     * アニメ視聴状況リポジトリを提供
     * 視聴進捗や評価データへのアクセスを抽象化します
     * 
     * @param animeStatusDao アニメ視聴状況データアクセス用DAO
     * @return AnimeStatusRepository
     */
    @Provides
    fun provideAnimeStatusRepository(animeStatusDao: AnimeStatusDao): AnimeStatusRepository {
        return AnimeStatusRepository(animeStatusDao)
    }
}
