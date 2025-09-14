package com.anichest.app.data.preferences

/**
 * テーマ設定の種類を定義するenum
 *
 * アプリのテーマモードを制御するための設定値です。
 * ユーザーはシステム設定に従うか、明示的にライト/ダークモードを選択できます。
 */
enum class ThemeMode {
    /**
     * システム設定に従う（デフォルト）
     *
     * OSのダークモード設定に自動的に追従します。
     * システムがダークモードの場合はダークテーマ、
     * ライトモードの場合はライトテーマが適用されます。
     */
    SYSTEM,

    /**
     * 常にライトモード
     *
     * システム設定に関係なく、常にライトテーマを使用します。
     */
    LIGHT,

    /**
     * 常にダークモード
     *
     * システム設定に関係なく、常にダークテーマを使用します。
     */
    DARK
}

/**
 * テーマ設定のデータクラス
 *
 * ユーザーのテーマ設定情報を保持します。
 * DataStoreによって永続化され、アプリ起動時に復元されます。
 *
 * @property themeMode 選択されたテーマモード
 */
data class ThemePreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)