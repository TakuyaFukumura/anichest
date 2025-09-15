# Anichest

[![Android CI](https://github.com/TakuyaFukumura/anichest/actions/workflows/ci.yml/badge.svg)](https://github.com/TakuyaFukumura/anichest/actions/workflows/ci.yml)
[![GitHub release](https://img.shields.io/github/v/release/TakuyaFukumura/anichest?include_prereleases)](https://github.com/TakuyaFukumura/anichest/releases)

KotlinとJetpack Composeで構築されたアニメ視聴管理アプリケーション。
アニメの視聴状況、評価、進捗を管理できるモダンなAndroidアプリです。

## 概要

このアプリは以下の機能を提供します：

### 🎯 主要機能
- **アニメ管理**: 作品の基本情報（タイトル、話数、ジャンル、年度、説明）を管理
- **視聴状況追跡**: 未視聴、視聴中、視聴済、中止の4つのステータスで管理
- **5段階評価システム**: 観た作品に1-5の評価を付けることが可能
- **視聴進捗管理**: 何話まで観たかを記録・追跡
- **検索・フィルタリング**: タイトル検索や視聴状況での絞り込み
- **統計表示**: 視聴中・完了作品数の統計情報

### 🏗️ アーキテクチャ
- **MVVM + Repository パターン**: ViewModel、Repository、DAOによる適切な責任分離
- **Room Database**: SQLiteベースのローカルデータベース
- **Jetpack Compose**: 宣言的UIによる効率的な開発
- **Material 3デザイン**: 最新のデザインガイドラインに準拠
- **StateFlow**: リアクティブなデータバインディング

## 開発環境要件

- **Java**: 17
- **Android SDK**: API 36対応  
- **Kotlin**: 2.2.10
- **Android Gradle Plugin**: 7.4.2
- **ターゲットSDK**: 36
- **最小SDK**: 24（Android 7.0以上）

## 技術スタック

- **UI フレームワーク**: Jetpack Compose
- **デザインシステム**: Material 3
- **アーキテクチャ**: MVVM + Repository Pattern
- **データベース**: Room (SQLite)
- **非同期処理**: Kotlin Coroutines + StateFlow
- **ビルドシステム**: Gradle with Kotlin DSL
- **テスト**: JUnit 4 + Espresso

## セットアップと実行

### 前提条件

⚠️ **注意**: ビルドにはインターネット接続が必要です。
Android Gradle Pluginの依存関係をGoogleのMavenリポジトリからダウンロードする必要があります。

### 1. プロジェクトのクローン
```bash
git clone https://github.com/TakuyaFukumura/anichest.git
```
```bash
cd anichest
```

### 2. ビルドと実行
- 実行権限の付与
```bash
chmod +x gradlew
```
- 依存関係の確認
```bash
./gradlew dependencies
```
- デバッグビルド（初回は時間がかかります）
```bash
./gradlew assembleDebug
```
- デバイスまたはエミュレーターにインストール
```bash
./gradlew installDebug
```

### 3. 開発用のタスク
- Lintチェック
```bash
./gradlew lintDebug
```
- ユニットテスト実行
```bash
./gradlew testDebugUnitTest
```
- 全体的なコード品質チェック
```bash
./gradlew check
```

### トラブルシューティング

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
anichest/
├── app/                           # メインアプリケーションモジュール
│   ├── src/main/
│   │   ├── java/com/anichest/app/
│   │   │   ├── MainActivity.kt    # メインアクティビティ
│   │   │   ├── AnichestApplication.kt # アプリケーションクラス
│   │   │   ├── data/              # データ層
│   │   │   │   ├── entity/        # Roomエンティティ
│   │   │   │   │   ├── Anime.kt   # アニメ情報
│   │   │   │   │   └── AnimeStatus.kt # 視聴状況
│   │   │   │   ├── dao/           # データアクセスオブジェクト
│   │   │   │   ├── database/      # データベース設定
│   │   │   │   └── repository/    # リポジトリ層
│   │   │   └── ui/               # UI層
│   │   │       ├── screen/       # 画面コンポーネント
│   │   │       ├── viewmodel/    # ViewModelクラス
│   │   │       └── theme/        # UIテーマ設定
│   │   ├── res/                  # Androidリソース
│   │   └── AndroidManifest.xml   # アプリマニフェスト
│   ├── src/test/                 # ユニットテスト
│   └── src/androidTest/          # インストルメンテーションテスト
```

## バージョン履歴

- **v2.0.0**: ウィッシュリスト機能の削除
  - ウィッシュリスト機能を完全に削除
  - データベーススキーマの簡素化
  - UIからウィッシュリスト関連の画面とボタンを削除
  - アプリケーションの軽量化とシンプル化

- **v0.1.0**: アニメ視聴管理機能の初期実装
  - アニメ情報管理（タイトル、話数、ジャンル、年度、説明）
  - 視聴状況管理（未視聴、視聴中、視聴済、中止）
  - 5段階評価システム
  - 視聴進捗追跡
  - MVVM + Repository パターン実装
  - Room Database統合
