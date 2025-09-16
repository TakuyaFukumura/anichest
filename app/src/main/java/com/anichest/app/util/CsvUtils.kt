package com.anichest.app.util

import android.content.Context
import android.net.Uri
import com.anichest.app.data.entity.Anime
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CSV形式でのアニメデータの出力・入力を行うユーティリティクラス
 */
object CsvUtils {

    private const val CSV_HEADER = "title,totalEpisodes,genre,year,description"
    private const val CSV_SEPARATOR = ","
    private const val CSV_QUOTE = "\""

    /**
     * 現在の日時を含むCSVファイル名を生成
     *
     * @return フォーマット: anime_export_YYYY-MM-DD_HH-mm-ss.csv
     */
    fun generateCsvFileName(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "anime_export_$timestamp.csv"
    }

    /**
     * アニメデータリストをCSV形式の文字列に変換
     *
     * @param animeList エクスポートするアニメデータのリスト
     * @return CSV形式の文字列
     */
    fun exportToCsv(animeList: List<Anime>): String {
        val csvBuilder = StringBuilder()

        // ヘッダー行を追加
        csvBuilder.appendLine(CSV_HEADER)

        // データ行を追加
        animeList.forEach { anime ->
            val row = listOf(
                escapeField(anime.title),
                anime.totalEpisodes.toString(),
                escapeField(anime.genre),
                anime.year.toString(),
                escapeField(anime.description)
            ).joinToString(CSV_SEPARATOR)

            csvBuilder.appendLine(row)
        }

        return csvBuilder.toString()
    }

    /**
     * CSVファイルをアニメデータリストに変換
     *
     * @param context アプリケーションコンテキスト
     * @param uri CSVファイルのURI
     * @return パース結果を含むImportResult
     */
    suspend fun importFromCsv(context: Context, uri: Uri): ImportResult {
        val animeList = mutableListOf<Anime>()
        val errors = mutableListOf<String>()
        var lineNumber = 0

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                    // ヘッダー行をスキップ
                    val headerLine = reader.readLine()
                    lineNumber++

                    if (headerLine == null) {
                        errors.add("ファイルが空です")
                        return ImportResult(animeList, errors)
                    }

                    // データ行を処理
                    reader.lineSequence().forEach { line ->
                        lineNumber++

                        try {
                            val anime = parseCsvLine(line)
                            animeList.add(anime)
                        } catch (e: Exception) {
                            errors.add("行 $lineNumber: ${e.message}")
                        }
                    }
                }
            } ?: throw Exception("ファイルを開けませんでした")
        } catch (e: Exception) {
            errors.add("ファイル読み込みエラー: ${e.message}")
        }

        return ImportResult(animeList, errors)
    }

    /**
     * CSV行をパースしてAnimeオブジェクトを作成
     *
     * @param line CSV行の文字列
     * @return パースされたAnimeオブジェクト
     * @throws Exception パースエラーが発生した場合
     */
    private fun parseCsvLine(line: String): Anime {
        val fields = parseCsvFields(line)

        if (fields.size < 5) {
            throw Exception("必要なフィールドが不足しています（5フィールド必要、${fields.size}フィールド検出）")
        }

        val title = fields[0].trim()
        if (title.isEmpty()) {
            throw Exception("タイトルは必須です")
        }

        val totalEpisodes = try {
            fields[1].trim().toIntOrNull() ?: 0
        } catch (e: NumberFormatException) {
            0
        }

        val genre = fields[2].trim()

        val year = try {
            fields[3].trim().toIntOrNull() ?: 0
        } catch (e: NumberFormatException) {
            0
        }

        val description = fields[4].trim()

        return Anime(
            title = title,
            totalEpisodes = totalEpisodes,
            genre = genre,
            year = year,
            description = description
        )
    }

    /**
     * CSV行をフィールドに分割（引用符とエスケープを考慮）
     *
     * @param line CSV行の文字列
     * @return フィールドのリスト
     */
    private fun parseCsvFields(line: String): List<String> {
        val fields = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            when {
                char == CSV_QUOTE.first() && !inQuotes -> {
                    inQuotes = true
                }

                char == CSV_QUOTE.first() && inQuotes -> {
                    // 次の文字もクォートの場合はエスケープされたクォート
                    if (i + 1 < line.length && line[i + 1] == CSV_QUOTE.first()) {
                        currentField.append(CSV_QUOTE)
                        i++ // 次のクォートをスキップ
                    } else {
                        inQuotes = false
                    }
                }

                char == CSV_SEPARATOR.first() && !inQuotes -> {
                    fields.add(currentField.toString())
                    currentField.clear()
                }

                else -> {
                    currentField.append(char)
                }
            }
            i++
        }

        // 最後のフィールドを追加
        fields.add(currentField.toString())

        return fields
    }

    /**
     * CSVフィールドをエスケープ（引用符で囲み、内部の引用符をエスケープ）
     *
     * @param field エスケープするフィールド
     * @return エスケープされたフィールド
     */
    private fun escapeField(field: String): String {
        // フィールドにカンマ、改行、引用符が含まれている場合は引用符で囲む
        return if (field.contains(CSV_SEPARATOR) || field.contains("\n") || field.contains(CSV_QUOTE)) {
            CSV_QUOTE + field.replace(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE) + CSV_QUOTE
        } else {
            field
        }
    }

    /**
     * CSVインポート結果を保持するデータクラス
     *
     * @property animeList インポートされたアニメデータのリスト
     * @property errors エラーメッセージのリスト
     */
    data class ImportResult(
        val animeList: List<Anime>,
        val errors: List<String>
    )
}
