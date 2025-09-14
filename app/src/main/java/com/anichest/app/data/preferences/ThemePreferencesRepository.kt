package com.anichest.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * テーマ設定の永続化を管理するリポジトリ
 *
 * DataStoreを使用してユーザーのテーマ設定を保存・復元します。
 * DIコンテナによって管理されるシングルトンクラスです。
 *
 * 主な機能:
 * - テーマ設定の永続化保存
 * - テーマ設定の復元
 * - テーマ設定変更の監視
 *
 * @property context アプリケーションコンテキスト
 */
@Singleton
class ThemePreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        /**
         * DataStoreの名前
         */
        private const val DATASTORE_NAME = "theme_preferences"

        /**
         * テーマモード設定のキー
         */
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

        /**
         * DataStoreのファクトリプロパティ
         *
         * アプリケーションコンテキストにDataStoreインスタンスを関連付けます。
         */
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }

    /**
     * テーマ設定を監視するFlow
     *
     * DataStoreから取得したデータをThemePreferencesオブジェクトに変換して提供します。
     * 設定が変更されると自動的に新しい値が流れます。
     *
     * @return テーマ設定のFlow
     */
    val themePreferencesFlow: Flow<ThemePreferences> = context.dataStore.data
        .map { preferences ->
            val themeModeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            
            // 文字列からThemeModeに変換、無効な値の場合はSYSTEMを使用
            val themeMode = try {
                ThemeMode.valueOf(themeModeString)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }

            ThemePreferences(themeMode = themeMode)
        }

    /**
     * テーマモードを更新する
     *
     * 指定されたテーマモードをDataStoreに保存します。
     * この変更は即座にthemePreferencesFlowに反映されます。
     *
     * @param themeMode 保存するテーマモード
     */
    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }
}