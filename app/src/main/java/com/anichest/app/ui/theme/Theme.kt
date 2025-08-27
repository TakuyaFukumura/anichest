package com.anichest.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * ダークテーマ用のカラースキーム
 *
 * 暗い背景で使用される色の組み合わせを定義しています。
 * Color.ktで定義された80番台の色（明るい色）を使用します。
 *
 * ダークモードでは明るい色をメインに使用することで、
 * 暗い背景とのコントラストを確保し、視認性を向上させます。
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * ライトテーマ用のカラースキーム
 *
 * 明るい背景で使用される色の組み合わせを定義しています。
 * Color.ktで定義された40番台の色（暗い色）を使用します。
 *
 * ライトモードでは暗い色をメインに使用することで、
 * 明るい背景とのコントラストを確保し、読みやすさを向上させます。
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* 
     * その他のデフォルトカラーのカスタマイズ例（コメントアウト済み）
     * 
     * 必要に応じてこれらの色をカスタマイズできます：
     * 
     * background = Color(0xFFFFFBFE),     // 背景色
     * surface = Color(0xFFFFFBFE),        // サーフェス色
     * onPrimary = Color.White,            // プライマリ色の上のテキスト色
     * onSecondary = Color.White,          // セカンダリ色の上のテキスト色
     * onTertiary = Color.White,           // ターシャリ色の上のテキスト色
     * onBackground = Color(0xFF1C1B1F),   // 背景の上のテキスト色
     * onSurface = Color(0xFF1C1B1F),      // サーフェスの上のテキスト色
     */
)

/**
 * アプリケーションのメインテーマ関数
 *
 * このComposable関数はアプリ全体のデザインテーマを適用します。
 * Material Design 3の原則に従い、色、タイポグラフィ、形状などを統一します。
 *
 * 機能:
 * - システムのダークモード設定の自動検出
 * - Android 12以降でのDynamic Color対応
 * - カスタムカラースキームとタイポグラフィの適用
 *
 * @param darkTheme ダークテーマを使用するかどうか。
 *                  デフォルトはシステム設定に従います。
 *
 * @param dynamicColor Dynamic Color（動的な色）機能を使用するかどうか。
 *                     Android 12以降で、ユーザーの壁紙から抽出した色を
 *                     アプリのテーマに適用する機能です。
 *                     デフォルトはtrue（有効）です。
 *
 * @param content このテーマが適用されるUIコンテンツ。
 *                通常はアプリのメインコンテンツが渡されます。
 */
@Composable
fun AnichestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color は Android 12以降で利用可能
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 使用するカラースキームを決定
    val colorScheme = when {
        // Dynamic Colorが有効かつAndroid 12以降の場合
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // システムから動的に色を取得
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // ダークテーマの場合
        darkTheme -> DarkColorScheme
        // ライトテーマの場合
        else -> LightColorScheme
    }

    // Material Themeを適用
    MaterialTheme(
        colorScheme = colorScheme,    // カラースキーム
        typography = Typography,      // タイポグラフィ
        content = content            // UIコンテンツ
    )
}
