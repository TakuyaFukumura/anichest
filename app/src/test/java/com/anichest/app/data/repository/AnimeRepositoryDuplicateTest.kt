package com.anichest.app.data.repository

import com.anichest.app.data.dao.AnimeDao
import com.anichest.app.data.entity.Anime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * AnimeRepositoryの重複チェック機能のテスト
 */
class AnimeRepositoryDuplicateTest {

    @Mock
    private lateinit var animeDao: AnimeDao

    private lateinit var repository: AnimeRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = AnimeRepository(animeDao)
    }

    @Test
    fun `insertAnimeWithValidation_正常なタイトル_成功する`() = runTest {
        // Arrange
        val anime = Anime(title = "進撃の巨人", totalEpisodes = 25)
        `when`(animeDao.existsByTitle("進撃の巨人")).thenReturn(false)
        `when`(animeDao.insertAnime(anime)).thenReturn(1L)

        // Act
        val result = repository.insertAnimeWithValidation(anime)

        // Assert
        assertEquals(1L, result)
    }

    @Test
    fun `insertAnimeWithValidation_空のタイトル_InvalidTitleExceptionをスロー`() = runTest {
        // Arrange
        val anime = Anime(title = "", totalEpisodes = 25)

        // Act & Assert
        try {
            repository.insertAnimeWithValidation(anime)
            fail("InvalidTitleException が期待されました")
        } catch (e: InvalidTitleException) {
            assertEquals("アニメタイトルは必須です", e.message)
        }
    }

    @Test
    fun `insertAnimeWithValidation_重複タイトル_DuplicateTitleExceptionをスロー`() = runTest {
        // Arrange
        val anime = Anime(title = "進撃の巨人", totalEpisodes = 25)
        `when`(animeDao.existsByTitle("進撃の巨人")).thenReturn(true)

        // Act & Assert
        try {
            repository.insertAnimeWithValidation(anime)
            fail("DuplicateTitleException が期待されました")
        } catch (e: DuplicateTitleException) {
            assertEquals("「進撃の巨人」は既に登録されています", e.message)
        }
    }

    @Test
    fun `insertAnimeWithValidation_前後に空白があるタイトル_トリムして処理`() = runTest {
        // Arrange
        val anime = Anime(title = "  進撃の巨人  ", totalEpisodes = 25)
        val trimmedAnime = anime.copy(title = "進撃の巨人")
        `when`(animeDao.existsByTitle("進撃の巨人")).thenReturn(false)
        `when`(animeDao.insertAnime(trimmedAnime)).thenReturn(1L)

        // Act
        val result = repository.insertAnimeWithValidation(anime)

        // Assert
        assertEquals(1L, result)
    }
}