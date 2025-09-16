package com.anichest.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.dao.AnimeStatusDao
import com.anichest.app.data.entity.Anime
import com.anichest.app.data.entity.AnimeStatus
import com.anichest.app.data.entity.WatchStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * アニメ視聴管理アプリのメインデータベースクラス
 *
 * Roomデータベースの中心となるクラスで、以下の機能を提供する：
 * - アニメ作品情報の管理
 * - 視聴状況の追跡
 * - データベースの初期化とサンプルデータの投入
 *
 * シングルトンパターンを使用してアプリケーション全体で
 * 単一のデータベースインスタンスを共有する。
 *
 * @see AnimeDao アニメ作品データアクセス
 * @see AnimeStatusDao 視聴状況データアクセス
 */
@Database(
    entities = [
        Anime::class,
        AnimeStatus::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * アニメ作品データへのアクセスを提供するDAO
     *
     * @return AnimeDao インスタンス
     */
    abstract fun animeDao(): AnimeDao

    /**
     * アニメ視聴状況データへのアクセスを提供するDAO
     *
     * @return AnimeStatusDao インスタンス
     */
    abstract fun animeStatusDao(): AnimeStatusDao

    companion object {

        /**
         * データベースインスタンス
         *
         * @Volatile により複数スレッドからの安全なアクセスを保証
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * データベースインスタンスを取得する
         *
         * シングルトンパターンを使用してアプリケーション全体で
         * 単一のデータベースインスタンスを提供する。
         * スレッドセーフな実装により、複数スレッドからの
         * 同時アクセスでも安全にインスタンスを取得できる。
         *
         * @param context アプリケーションコンテキスト
         * @param scope データベース初期化で使用するコルーチンスコープ
         * @return AppDatabase インスタンス
         */
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DatabaseConstants.DATABASE_NAME
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * データベース初期化コールバック
     *
     * データベースが初回作成された際に実行される処理を定義する。
     * 主にサンプルデータの投入を行い、アプリケーションの
     * 初期状態を整える役割を持つ。
     *
     * @param scope コルーチンスコープ（非同期処理で使用）
     */
    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        /**
         * データベース作成時のコールバック
         *
         * データベースが初回作成された際に自動的に実行される。
         * サンプルデータの投入を非同期で実行することで、
         * UIスレッドをブロックしないようにしている。
         *
         * @param db 作成されたデータベースインスタンス
         */
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
         *
         * データベースが空の場合のみサンプルデータを投入する。
         * 既存データがある場合は重複を避けるため処理をスキップする。
         * アプリケーションの初期状態として、架空のアニメ作品データと
         * それに対応する視聴状況データを提供し、ユーザーが機能を試せる環境を構築する。
         *
         * @param database データベースインスタンス
         */
        private suspend fun populateSampleData(database: AppDatabase) {
            val animeDao = database.animeDao()
            val animeStatusDao = database.animeStatusDao()

            // 既存データの確認 - データが存在する場合は投入をスキップ
            if (animeDao.getAnimeCount() > 0) {
                return
            }

            // 架空のアニメ作品サンプルデータ
            val sampleAnimes = listOf(
                Anime(
                    title = "星空の騎士団",
                    totalEpisodes = 24,
                    genre = "ファンタジー,アドベンチャー",
                    year = 2023,
                    description = "魔法の力を失った世界で、星の力を宿した騎士たちが闇の勢力と戦う壮大な冒険物語"
                ),
                Anime(
                    title = "未来学園アカデミー",
                    totalEpisodes = 12,
                    genre = "SF,学園",
                    year = 2024,
                    description = "時空を超える技術を学ぶ特殊な学園で、生徒たちが歴史を守るために奮闘する"
                ),
                Anime(
                    title = "ドラゴンハート・クロニクル",
                    totalEpisodes = 36,
                    genre = "ファンタジー,アクション",
                    year = 2022,
                    description = "古代ドラゴンとの契約を結んだ少年が、世界の平和を取り戻すために立ち上がる物語"
                ),
                Anime(
                    title = "月光探偵事務所",
                    totalEpisodes = 20,
                    genre = "ミステリー,サスペンス",
                    year = 2023,
                    description = "超常現象専門の探偵事務所を舞台に、不可思議な事件を解決していく推理アニメ"
                ),
                Anime(
                    title = "虹色マジカルガールズ",
                    totalEpisodes = 48,
                    genre = "魔法少女,コメディ",
                    year = 2024,
                    description = "7人の魔法少女が色とりどりの魔法で街の平和を守る、笑いと感動の物語"
                )
            )

            // サンプルデータの投入と視聴状況の作成
            sampleAnimes.forEachIndexed { index, anime ->
                // アニメデータを挿入し、IDを取得
                val animeId = animeDao.insertAnime(anime)
                
                // 各アニメに対応するサンプル視聴状況データを作成
                val sampleStatus = when (index) {
                    0 -> AnimeStatus(
                        animeId = animeId,
                        status = WatchStatus.COMPLETED,
                        rating = 5,
                        review = "魔法と冒険の素晴らしい作品。キャラクターの成長が感動的でした。",
                        watchedEpisodes = 24
                    )
                    1 -> AnimeStatus(
                        animeId = animeId,
                        status = WatchStatus.WATCHING,
                        rating = 0,
                        review = "",
                        watchedEpisodes = 8
                    )
                    2 -> AnimeStatus(
                        animeId = animeId,
                        status = WatchStatus.COMPLETED,
                        rating = 4,
                        review = "ドラゴンとの絆が印象的な作品。バトルシーンも迫力があります。",
                        watchedEpisodes = 36
                    )
                    3 -> AnimeStatus(
                        animeId = animeId,
                        status = WatchStatus.DROPPED,
                        rating = 2,
                        review = "設定は面白いが、展開が少し物足りなかった。",
                        watchedEpisodes = 5
                    )
                    4 -> AnimeStatus(
                        animeId = animeId,
                        status = WatchStatus.UNWATCHED,
                        rating = 0,
                        review = "",
                        watchedEpisodes = 0
                    )
                    else -> AnimeStatus(
                        animeId = animeId,
                        status = WatchStatus.UNWATCHED,
                        rating = 0,
                        review = "",
                        watchedEpisodes = 0
                    )
                }
                
                // 視聴状況データを挿入
                animeStatusDao.insertOrUpdateStatus(sampleStatus)
            }
        }
    }
}
