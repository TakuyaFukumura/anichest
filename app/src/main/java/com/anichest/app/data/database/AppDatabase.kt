package com.anichest.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.dao.StringDao
import com.anichest.app.data.dao.WishlistDao
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.StringEntity
import com.anichest.app.data.entity.WishlistItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * アニメ視聴管理アプリのメインデータベースクラス
 */
@Database(
    entities = [
        Anime::class,
        AnimeStatus::class,
        WishlistItem::class,
        StringEntity::class // 後方互換性のため残す
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao
    abstract fun animeStatusDao(): AnimeStatusDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun stringDao(): StringDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "anichest_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * データベース初期化コールバック
     */
    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch {
                    // サンプルデータの投入
                    populateSampleData(database)
                }
            }
        }

        /**
         * サンプルデータを投入
         */
        private suspend fun populateSampleData(database: AppDatabase) {
            val animeDao = database.animeDao()

            // サンプルアニメデータ
            val sampleAnimes = listOf(
                Anime(
                    title = "鬼滅の刃",
                    totalEpisodes = 26,
                    genre = "アクション,歴史",
                    year = 2019,
                    description = "家族を鬼に殺された少年・炭治郎が鬼殺隊として鬼と戦う物語"
                ),
                Anime(
                    title = "呪術廻戦",
                    totalEpisodes = 24,
                    genre = "アクション,超自然",
                    year = 2020,
                    description = "呪術師として呪いと戦う高校生たちの物語"
                ),
                Anime(
                    title = "SPY×FAMILY",
                    totalEpisodes = 12,
                    genre = "コメディ,アクション",
                    year = 2022,
                    description = "スパイ、殺し屋、超能力者による偽装家族の日常"
                )
            )

            sampleAnimes.forEach { anime ->
                animeDao.insertAnime(anime)
            }
        }
    }
}
