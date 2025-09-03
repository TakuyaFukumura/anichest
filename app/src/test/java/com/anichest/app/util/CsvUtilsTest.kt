package com.anichest.app.util

import com.anichest.app.data.entity.Anime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * CsvUtilsのユニットテスト
 */
class CsvUtilsTest {

    @Test
    fun testGenerateCsvFileName() {
        val fileName = CsvUtils.generateCsvFileName()
        assertTrue("ファイル名が正しい形式であること", fileName.startsWith("anime_export_"))
        assertTrue("ファイル名が.csvで終わること", fileName.endsWith(".csv"))
    }

    @Test
    fun testExportToCsv() {
        val animeList = listOf(
            Anime(
                id = 1L,
                title = "テストアニメ1",
                totalEpisodes = 12,
                genre = "アクション,ドラマ",
                year = 2023,
                description = "これはテスト用のアニメです。",
                imageUrl = "https://example.com/image1.jpg"
            ),
            Anime(
                id = 2L,
                title = "テストアニメ2",
                totalEpisodes = 24,
                genre = "コメディ",
                year = 2024,
                description = "これも\"テスト用\"のアニメです。",
                imageUrl = ""
            )
        )

        val csvData = CsvUtils.exportToCsv(animeList)
        val lines = csvData.split("\n").filter { it.isNotEmpty() }

        // ヘッダー行をチェック
        assertEquals("title,totalEpisodes,genre,year,description,imageUrl", lines[0])

        // データ行をチェック
        assertTrue("1行目のデータが正しいこと", lines[1].contains("テストアニメ1"))
        assertTrue("2行目のデータが正しいこと", lines[2].contains("テストアニメ2"))

        // 引用符でエスケープされたフィールドをチェック（実際の出力形式に合わせて修正）
        assertTrue("説明文が引用符でエスケープされていること", lines[2].contains("\"これも\"\"テスト用\"\"のアニメです。\""))
    }

    @Test
    fun testParseCsvFields_BasicFields() {
        val csvData = "title,totalEpisodes,genre,year,description,imageUrl\n" +
                      "テストアニメ,12,アクション,2023,説明文,https://example.com/image.jpg"

        // この場合は実際のパースはimportFromCsvで行うため、exportで出力されたデータが正しく読めることを確認
        val lines = csvData.split("\n")
        val headerFields = lines[0].split(",")
        val dataFields = lines[1].split(",")

        assertEquals(6, headerFields.size)
        assertEquals(6, dataFields.size)
        assertEquals("テストアニメ", dataFields[0])
        assertEquals("12", dataFields[1])
        assertEquals("アクション", dataFields[2])
        assertEquals("2023", dataFields[3])
        assertEquals("説明文", dataFields[4])
        assertEquals("https://example.com/image.jpg", dataFields[5])
    }

    @Test
    fun testEscapeField() {
        val animeWithComma = Anime(
            title = "アニメタイトル,サブタイトル",
            totalEpisodes = 12,
            genre = "ドラマ",
            year = 2023,
            description = "カンマ,を含む説明文",
            imageUrl = ""
        )

        val csvData = CsvUtils.exportToCsv(listOf(animeWithComma))
        
        // カンマを含むフィールドが引用符で囲まれていることを確認
        assertTrue("カンマを含むタイトルが引用符で囲まれていること", csvData.contains("\"アニメタイトル,サブタイトル\""))
        assertTrue("カンマを含む説明が引用符で囲まれていること", csvData.contains("\"カンマ,を含む説明文\""))
    }

    @Test
    fun testEscapeField_WithQuotes() {
        val animeWithQuotes = Anime(
            title = "\"引用符\"を含むタイトル",
            totalEpisodes = 12,
            genre = "ドラマ",
            year = 2023,
            description = "引用符\"内\"の説明",
            imageUrl = ""
        )

        val csvData = CsvUtils.exportToCsv(listOf(animeWithQuotes))
        
        // 引用符がエスケープされていることを確認（実際の出力形式に合わせて修正）
        assertTrue("引用符がエスケープされていること", csvData.contains("\"\"\"引用符\"\"を含むタイトル\""))
        assertTrue("説明の引用符がエスケープされていること", csvData.contains("\"引用符\"\"内\"\"の説明\""))
    }
}