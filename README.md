# basic-android-app

KotlinとJetpack Composeで構築されたモダンなAndroidアプリケーション。シンプルな「Hello Android!」メッセージを表示するサンプルアプリです。

## 概要

このアプリは以下の機能を提供します：
- **メインスクリーン**: データベースから取得した文字列を使用した「Hello Android!」メッセージの表示
- **Roomデータベース統合**: SQLiteデータベースからの文字列データ取得
- **Jetpack Composeを使用したモダンなUI**: 宣言的UIによる効率的な開発
- **Material 3デザインシステムの採用**: 最新のデザインガイドラインに準拠
- **ダークテーマとライトテーマの対応**: システム設定に応じた自動切り替え
- **動的カラー（Android 12+）の対応**: デバイスの壁紙に基づいた色調整

### アプリの構成
- **MainActivity**: アプリのエントリーポイント、Composeを設定
- **MainViewModel**: データベースからの文字列取得を管理するViewModel
- **Greeting**: データベースから取得した文字列を表示するコンポーザブル関数
- **Room Database**: 文字列データを格納するSQLiteデータベース
- **Repository Pattern**: データアクセスの抽象化レイヤー
- **テーマシステム**: Color、Theme、Typeファイルによる一貫したデザイン

## 開発環境要件

- **Java**: 17
- **Android SDK**: API 36対応
- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 8.1.0
- **ターゲットSDK**: 36
- **最小SDK**: 24（Android 7.0以上）

## 技術スタック

- **UI フレームワーク**: Jetpack Compose (BOM 2024.09.00)
- **デザインシステム**: Material 3
- **アーキテクチャ**: MVVM + Repository Pattern
- **データベース**: Room (SQLite)
- **非同期処理**: Kotlin Coroutines + StateFlow
- **ビルドシステム**: Gradle 8.13 with Kotlin DSL
- **テスト**: JUnit 4 + Espresso

## セットアップと実行

### 前提条件

⚠️ **注意**: ビルドにはインターネット接続が必要です。Android Gradle Pluginの依存関係をGoogleのMavenリポジトリからダウンロードする必要があります。

### 1. プロジェクトのクローン
```bash
git clone https://github.com/TakuyaFukumura/basic-android-app.git
cd basic-android-app
```

### 2. ビルドと実行
```bash
# 実行権限の付与
chmod +x gradlew

# 依存関係の確認
./gradlew dependencies

# デバッグビルド（初回は時間がかかります）
./gradlew assembleDebug

# デバイスまたはエミュレーターにインストール
./gradlew installDebug
```

### 3. 開発用のタスク
```bash
# Lintチェック
./gradlew lintDebug

# ユニットテスト実行
./gradlew testDebugUnitTest

# 全体的なコード品質チェック
./gradlew check
```

### トラブルシューティング

- 初回ビルドでネットワークエラーが発生する場合は、インターネット接続を確認してください
- Gradleデーモンの問題が発生した場合は `./gradlew --stop` で停止後に再実行してください

## CI/CD設定

GitHub Actionsを使用した自動化されたビルドパイプラインが設定されています。

### 自動実行される処理

- **Lintチェック**: コード品質の検証
- **ユニットテスト**: 自動化されたテスト実行
- **デバッグビルド**: APKファイルの生成
- **依存関係キャッシュ**: ビルド時間の最適化

### ワークフロートリガー

`.github/workflows/ci.yml` で定義され、以下のタイミングで実行されます：
- 任意のブランチへのプッシュ時
- 並行実行制御により重複実行を防止

## プロジェクト構造

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt              # メインアクティビティ
├── MyApplication.kt             # アプリケーションクラス（DB初期化）
├── data/                        # データレイヤー
│   ├── entity/
│   │   └── StringEntity.kt      # データベースエンティティ
│   ├── dao/
│   │   └── StringDao.kt         # データアクセスオブジェクト
│   ├── database/
│   │   └── AppDatabase.kt       # Roomデータベース設定
│   └── repository/
│       └── StringRepository.kt  # リポジトリパターン実装
├── ui/
│   ├── viewmodel/
│   │   └── MainViewModel.kt     # ViewModelクラス
│   └── theme/                   # UIテーマ設定
│       ├── Color.kt             # カラーパレット
│       ├── Theme.kt             # Material 3テーマ
│       └── Type.kt              # タイポグラフィ設定
```

## バージョン履歴

- **v0.3.0**: Roomデータベース統合、MVVM+Repository パターンの実装
- **v0.2.0**: Java 17への移行、最新依存関係への更新
- **v0.1.0**: 初期バージョン（Java 11ベース）
