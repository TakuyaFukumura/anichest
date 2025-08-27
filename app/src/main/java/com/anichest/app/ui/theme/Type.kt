package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * アプリケーションのタイポグラフィ設定
 *
 * Material Design 3のタイポグラフィシステムに基づいて、
 * アプリ全体で使用されるテキストスタイルを定義しています。
 *
 * 現在は bodyLarge のみカスタマイズされており、
 * 他のテキストスタイル（titleLarge、labelSmallなど）は
 * Material3のデフォルト値が使用されます。
 *
 * カスタマイズされたスタイル:
 * - bodyLarge: 本文用の基本テキストスタイル
 */
// Material Design のタイポグラフィスタイルセット
val Typography = Typography(
    /**
     * 本文用の大きなテキストスタイル
     *
     * アプリのメインコンテンツや長い文章で使用されます。
     * 読みやすさを重視した設定になっています。
     *
     * 設定詳細:
     * - フォントファミリー: システムデフォルト
     * - フォントウェイト: Normal（400）
     * - フォントサイズ: 16sp
     * - 行の高さ: 24sp
     * - 文字間隔: 0.5sp
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* 
     * その他のデフォルトテキストスタイルの例（コメントアウト済み）
     * 
     * 必要に応じてこれらのスタイルをカスタマイズできます：
     * 
     * titleLarge = TextStyle(
     *     fontFamily = FontFamily.Default,
     *     fontWeight = FontWeight.Normal,
     *     fontSize = 22.sp,
     *     lineHeight = 28.sp,
     *     letterSpacing = 0.sp
     * ),
     * labelSmall = TextStyle(
     *     fontFamily = FontFamily.Default,
     *     fontWeight = FontWeight.Medium,
     *     fontSize = 11.sp,
     *     lineHeight = 16.sp,
     *     letterSpacing = 0.5.sp
     * )
     */
)
