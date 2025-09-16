package com.anichest.app.ui.viewmodel

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

/**
 * AnimeFilterCriteriaのテストクラス
 */
class AnimeFilterCriteriaTest {

    @Test
    fun `デフォルトコンストラクタでは全てのフィルターがnull`() {
        val criteria = AnimeFilterCriteria()
        assertNull(criteria.yearRange)
        assertNull(criteria.ratingRange)
    }

    @Test
    fun `年範囲フィルターが正しく設定される`() {
        val yearRange = 2020..2023
        val criteria = AnimeFilterCriteria(yearRange = yearRange)
        assertEquals(yearRange, criteria.yearRange)
        assertNull(criteria.ratingRange)
    }

    @Test
    fun `評価範囲フィルターが正しく設定される`() {
        val ratingRange = 3..5
        val criteria = AnimeFilterCriteria(ratingRange = ratingRange)
        assertEquals(ratingRange, criteria.ratingRange)
        assertNull(criteria.yearRange)
    }

    @Test
    fun `年と評価の両方のフィルターが正しく設定される`() {
        val yearRange = 2020..2023
        val ratingRange = 4..5
        val criteria = AnimeFilterCriteria(yearRange = yearRange, ratingRange = ratingRange)
        assertEquals(yearRange, criteria.yearRange)
        assertEquals(ratingRange, criteria.ratingRange)
    }

    @Test
    fun `copyメソッドで部分的な更新ができる`() {
        val originalCriteria = AnimeFilterCriteria(yearRange = 2020..2022)
        val updatedCriteria = originalCriteria.copy(ratingRange = 3..5)
        
        assertEquals(2020..2022, updatedCriteria.yearRange)
        assertEquals(3..5, updatedCriteria.ratingRange)
    }

    @Test
    fun `copyメソッドでフィルターをクリアできる`() {
        val originalCriteria = AnimeFilterCriteria(
            yearRange = 2020..2022, 
            ratingRange = 3..5
        )
        val clearedCriteria = originalCriteria.copy(yearRange = null, ratingRange = null)
        
        assertNull(clearedCriteria.yearRange)
        assertNull(clearedCriteria.ratingRange)
    }
}