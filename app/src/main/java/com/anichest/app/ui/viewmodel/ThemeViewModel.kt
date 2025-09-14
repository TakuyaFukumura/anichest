package com.anichest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anichest.app.data.preferences.ThemeMode
import com.anichest.app.data.preferences.ThemePreferences
import com.anichest.app.data.preferences.ThemePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * テーマ設定を管理するViewModel
 *
 * アプリ全体のテーマ状態を管理し、UIからのテーマ変更リクエストを処理します。
 * Hilt DIによって管理され、ThemePreferencesRepositoryと連携してデータの永続化を行います。
 *
 * 主な機能:
 * - 現在のテーマ設定の提供
 * - テーマモード変更の処理
 * - テーマ設定の永続化管理
 *
 * @property themePreferencesRepository テーマ設定の永続化を管理するリポジトリ
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferencesRepository: ThemePreferencesRepository
) : ViewModel() {

    /**
     * 内部用のテーマ設定状態
     */
    private val _themePreferences = MutableStateFlow(ThemePreferences())

    /**
     * 公開用のテーマ設定状態
     *
     * UIから読み取り専用でアクセス可能なテーマ設定のStateFlowです。
     * テーマ設定が変更されるとUIに自動的に反映されます。
     */
    val themePreferences: StateFlow<ThemePreferences> = _themePreferences.asStateFlow()

    init {
        // ViewModelの初期化時に保存されたテーマ設定を読み込み
        viewModelScope.launch {
            themePreferencesRepository.themePreferencesFlow.collect { preferences ->
                _themePreferences.value = preferences
            }
        }
    }

    /**
     * テーマモードを変更する
     *
     * 指定されたテーマモードに変更し、永続化します。
     * 変更は即座にUIに反映されます。
     *
     * @param themeMode 新しいテーマモード
     */
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            themePreferencesRepository.updateThemeMode(themeMode)
        }
    }

    /**
     * 次のテーマモードに切り替える
     *
     * 現在のテーマモードに応じて次のモードに順番に切り替えます：
     * SYSTEM → LIGHT → DARK → SYSTEM → ...
     *
     * ユーザーがボタンを押すたびに異なるテーマモードに切り替わります。
     */
    fun toggleThemeMode() {
        val currentMode = _themePreferences.value.themeMode
        val nextMode = when (currentMode) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
        updateThemeMode(nextMode)
    }
}