package com.anichest.app.ui.viewmodel

import com.anichest.app.data.preferences.ThemeMode
import com.anichest.app.data.preferences.ThemePreferences
import com.anichest.app.data.preferences.ThemePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * ThemeViewModelのユニットテスト
 *
 * テーマ設定の変更とモード切り替えの動作を検証します。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    @Mock
    private lateinit var themePreferencesRepository: ThemePreferencesRepository

    private lateinit var viewModel: ThemeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // モックの初期設定
        `when`(themePreferencesRepository.themePreferencesFlow)
            .thenReturn(flowOf(ThemePreferences(ThemeMode.SYSTEM)))

        viewModel = ThemeViewModel(themePreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `初期状態ではシステムテーマが設定されている`() {
        assertEquals(ThemeMode.SYSTEM, viewModel.themePreferences.value.themeMode)
    }

    @Test
    fun `テーマモード更新が正しく呼び出される`() = runTest {
        // テスト実行
        viewModel.updateThemeMode(ThemeMode.DARK)

        // 検証
        verify(themePreferencesRepository).updateThemeMode(ThemeMode.DARK)
    }

    @Test
    fun `システムからライトモードに切り替わる`() = runTest {
        // 準備
        `when`(themePreferencesRepository.themePreferencesFlow)
            .thenReturn(flowOf(ThemePreferences(ThemeMode.SYSTEM)))

        // テスト実行
        viewModel.toggleThemeMode()

        // 検証
        verify(themePreferencesRepository).updateThemeMode(ThemeMode.LIGHT)
    }

    @Test
    fun `ライトからダークモードに切り替わる`() = runTest {
        // 準備
        `when`(themePreferencesRepository.themePreferencesFlow)
            .thenReturn(flowOf(ThemePreferences(ThemeMode.LIGHT)))

        val viewModel = ThemeViewModel(themePreferencesRepository)

        // テスト実行
        viewModel.toggleThemeMode()

        // 検証
        verify(themePreferencesRepository).updateThemeMode(ThemeMode.DARK)
    }

    @Test
    fun `ダークからシステムモードに切り替わる`() = runTest {
        // 準備
        `when`(themePreferencesRepository.themePreferencesFlow)
            .thenReturn(flowOf(ThemePreferences(ThemeMode.DARK)))

        val viewModel = ThemeViewModel(themePreferencesRepository)

        // テスト実行
        viewModel.toggleThemeMode()

        // 検証
        verify(themePreferencesRepository).updateThemeMode(ThemeMode.SYSTEM)
    }
}
