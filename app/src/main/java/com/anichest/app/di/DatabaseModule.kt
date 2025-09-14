package com.anichest.app.di

import android.content.Context
import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.dao.AnimeStatusDao
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
 *
 * アプリケーション全体で使用されるデータベースとDAOのインスタンスを
 * シングルトンとして提供します。
 *
 * @see AppDatabase
 * @see AnimeDao
 * @see AnimeStatusDao
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * アプリケーション全体で使用するCoroutineScopeを提供
     * データベースの初期化処理などで使用されます
     *
     * @return SupervisorJobを使用したCoroutineScope
     */
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    /**
     * アプリケーションデータベースインスタンスを提供
     * シングルトンとして作成され、アプリケーション全体で共有されます
     *
     * @param context アプリケーションコンテキスト
     * @param applicationScope データベース初期化用のCoroutineScope
     * @return AppDatabaseのインスタンス
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, applicationScope)
    }

    /**
     * アニメデータアクセス用DAOを提供
     *
     * @param database AppDatabaseインスタンス
     * @return AnimeDao
     */
    @Provides
    fun provideAnimeDao(database: AppDatabase): AnimeDao {
        return database.animeDao()
    }

    /**
     * アニメ視聴状況データアクセス用DAOを提供
     *
     * @param database AppDatabaseインスタンス
     * @return AnimeStatusDao
     */
    @Provides
    fun provideAnimeStatusDao(database: AppDatabase): AnimeStatusDao {
        return database.animeStatusDao()
    }
}
