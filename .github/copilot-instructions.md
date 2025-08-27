# Basic Android App
KotlinとJetpack Composeで構築されたモダンなAndroidアプリケーション。これはAndroidアプリ開発の基盤を提供する基本的なAndroid開発テンプレートリポジトリです。

常にこれらの手順を最初に参照し、ここの情報と一致しない予期しない情報に遭遇した場合のみ、検索やbashコマンドにフォールバックしてください。

## 効果的な作業方法

### 前提条件とセットアップ
- Java 17が必要です（この環境で利用可能）
- Android SDKは `/usr/local/lib/android/sdk` にプリインストールされています
- Android platform-toolsとbuild-toolsが利用可能です
- Gradleラッパーがプロジェクトに含まれています

### ブートストラップとビルドコマンド
重要：すべてのAndroidビルドコマンドに適切なタイムアウトを設定してください。Androidビルドは特に初回実行時に大幅な時間がかかる場合があります。

**初期設定:**
```bash
# gradlewを実行可能にする（必要に応じて）
chmod +x gradlew

# AndroidツールをPATHに追加
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
```

**ビルドコマンド:**
```bash
# クリーンビルド（推奨される最初のステップ）
./gradlew clean
# デバッグAPKをビルド - 絶対にキャンセルしないこと：初回ビルドで15-45分かかる可能性があります。タイムアウトを60分以上に設定してください。
./gradlew assembleDebug
# リリースAPKをビルド - 絶対にキャンセルしないこと：20-60分かかる可能性があります。タイムアウトを90分以上に設定してください。
./gradlew assembleRelease
```

**テストコマンド:**
```bash
# ユニットテストを実行 - 絶対にキャンセルしないこと：5-15分かかります。タイムアウトを30分以上に設定してください。
./gradlew test
# Lintチェックを実行 - 絶対にキャンセルしないこと：10-30分かかります。タイムアウトを45分以上に設定してください。
./gradlew lint
# すべてのコード品質ツールをチェック - 絶対にキャンセルしないこと：15-45分かかります。タイムアウトを60分以上に設定してください。
./gradlew check
```

**インストールと実行:**
```bash
# 接続されたデバイス/エミュレーターにデバッグAPKをインストール
./gradlew installDebug
# アプリをアンインストール
adb uninstall com.example.myapplication
```

### 既知の問題と制限事項
**重要：ネットワークアクセスが必要**
- `gradle/libs.versions.toml` 内のAndroid Gradle Plugin バージョン (8.12.1) は、ネットワークアクセスが制限された環境では利用できない場合があります
- ビルドが「Plugin was not found」エラーで失敗する場合、これはGoogleのMavenリポジトリとのネットワーク接続問題を示しています
- そのような場合は、`gradle/libs.versions.toml` でより安定したAGPバージョン（例：8.1.0 または 7.4.2）への更新を検討してください

**一般的なビルド問題:**
- 初回ビルドはSDKコンポーネントの不足により失敗することがあります - ビルドコマンドを再実行してください
- Gradleデーモンの問題：`./gradlew --stop` を実行してから再試行してください
- ネットワークタイムアウト：`gradle.properties` でタイムアウト値を増加させてください
- メモリ問題：十分なヒープ空間を確保してください（すでに2048mに設定済み）

**ビルド検証ステータス:**
- ⚠️  この環境でのネットワーク制限により、ビルドコマンドを完全に検証できませんでした
- ✅  プロジェクト構造と設定が検証されました
- ✅  Android SDK環境が利用可能であることを確認しました（Android API 36 target）
- ✅  テストファイルとソースコードが検証されました
- ✅  依存関係とバージョンが分析されました

## 検証とテスト

### 手動検証要件
アプリケーションに変更を加えた後：

1. **常にプロジェクトをビルドして** コードがコンパイルされることを確認してください:
   ```bash
   ./gradlew assembleDebug
   ```

2. **ユニットテストを実行して** 機能を検証してください:
   ```bash
   ./gradlew test
   ```

3. **インストルメンテーションテストを実行してください**（エミュレーターまたはデバイスが必要）:
   ```bash
   ./gradlew connectedAndroidTest
   ```

4. **ユーザーシナリオを手動でテストしてください:**
   - デバイス/エミュレーターにアプリをインストール
   - アプリを起動し、「Hello Android!」テキストが表示されることを確認
   - 基本的なナビゲーションとUI操作をテスト
   - 基本的な使用中にアプリがクラッシュしないことを確認

### LintとコードクオリティA
変更をコミットする前に、常にこれらを実行してください:
```bash
# Android lintを実行
./gradlew lint
# すべてのチェックを実行（lint + テスト）
./gradlew check
```

## プロジェクト構造とナビゲーション

### 主要ディレクトリとファイル
```
basic-android-app/
├── app/                           # メインアプリケーションモジュール
│   ├── src/main/
│   │   ├── java/com/example/myapplication/
│   │   │   ├── MainActivity.kt    # Compose UIを使用するメインアクティビティ
│   │   │   └── ui/theme/         # テーマ設定
│   │   ├── res/                  # Androidリソース
│   │   └── AndroidManifest.xml   # アプリマニフェスト
│   ├── src/test/                 # ユニットテスト
│   ├── src/androidTest/          # インストルメンテーションテスト
│   ├── build.gradle.kts          # アプリレベルのビルド設定
│   └── proguard-rules.pro        # ProGuard設定
├── gradle/
│   └── libs.versions.toml        # 依存関係バージョンカタログ
├── build.gradle.kts              # プロジェクトレベルのビルドファイル
├── settings.gradle.kts           # Gradle設定
└── gradlew / gradlew.bat         # Gradleラッパースクリプト
```

### 監視すべき重要ファイル
変更を行う際は、これらのファイルが影響を受けるかどうか常に確認してください:
- `app/src/main/java/com/example/myapplication/MainActivity.kt` - メインアプリロジック
- `app/build.gradle.kts` - 依存関係とビルド設定
- `gradle/libs.versions.toml` - バージョン管理
- `app/src/main/AndroidManifest.xml` - アプリ権限と設定

### 一般的な開発タスク

**依存関係の追加:**
1. `gradle/libs.versions.toml` の `[libraries]` セクションに追加
2. `app/build.gradle.kts` の依存関係ブロックで参照
3. プロジェクトを同期してビルド

**新しいアクティビティ/画面の作成:**
1. `app/src/main/java/com/example/myapplication/` に新しいKotlinファイルを作成
2. Activityの場合は `AndroidManifest.xml` に追加
3. 必要に応じてナビゲーションまたはメインアクティビティを更新

**UIの変更:**
1. MainActivity.kt のCompose関数を編集するか、新しいcomposableを作成
2. 必要に応じて `ui/theme/` のテーマファイルを更新
3. デバイス/エミュレーターで即座にテスト

## 技術スタックと現在の設定
- **言語:** Kotlin 2.0.21
- **UIフレームワーク:** Jetpack Compose with BOM 2024.09.00
- **ビルドシステム:** Gradle 8.13 with Kotlin DSL
- **Android Gradle Plugin:** 8.12.1 (ネットワーク制限環境では調整が必要な場合があります)
- **Target SDK:** 36 (Android API level 36)
- **Min SDK:** 24 (Android 7.0)
- **Java互換性:** Java 17 (環境で利用可能)
- **パッケージ名:** com.example.myapplication
- **バージョン:** 0.1.0 (versionCode 1)

### 現在の依存関係 (libs.versions.toml より)
- androidx-core-ktx: 1.10.1
- androidx-lifecycle-runtime-ktx: 2.6.1
- androidx-activity-compose: 1.8.0
- androidx-compose-bom: 2024.09.00
- JUnit: 4.13.2
- androidx-junit: 1.1.5
- androidx-espresso-core: 3.5.1

## 開発ワークフロー
1. 作業開始前に常に最新の変更をプル
2. 段階的な変更を行い、頻繁にテスト
3. 重要な変更後にユニットテストを実行
4. UI変更をテストするためにデバイス/エミュレーターでビルドしてインストール
5. コミット前に完全なテストスイートを実行
6. 変更をプッシュする前に常にlintチェックを実行

**覚えておいてください：Androidビルドは時間がかかる場合があります。長時間実行されるビルド操作を絶対にキャンセルしないでください - 初回実行やクリーンビルドでは45分以上かかる場合があります。**

## クイックリファレンス - リポジトリ内容

### ルートディレクトリ
```
.github/               # GitHub設定とテンプレート
├── ISSUE_TEMPLATE/   # 課題テンプレート
└── copilot-instructions.md  # このファイル
app/                  # メインAndroidアプリケーションモジュール
gradle/              # Gradleバージョンカタログとラッパー
├── libs.versions.toml  # 集中化された依存関係バージョン
└── wrapper/         # Gradleラッパーファイル
build.gradle.kts     # プロジェクトレベルのビルド設定
settings.gradle.kts  # Gradle設定とモジュール組み込み
gradle.properties    # Gradle JVMとプロジェクト設定
gradlew             # Gradleラッパースクリプト（Unix）
gradlew.bat         # Gradleラッパースクリプト（Windows）
README.md           # プロジェクトドキュメント
.gitignore          # Git除外パターン
```

### アプリケーションモジュール (app/)
```
src/main/
├── java/com/example/myapplication/
│   ├── MainActivity.kt           # Compose UIを使用するメインアクティビティ
│   └── ui/theme/                # UIテーマ設定
│       ├── Color.kt             # アプリカラー定義
│       ├── Theme.kt             # Material3テーマセットアップ
│       └── Type.kt              # タイポグラフィ定義
├── res/                         # Androidリソース
│   ├── values/                  # 文字列とスタイルリソース
│   ├── mipmap-*/               # アプリアイコン（各種密度）
│   └── xml/                    # バックアップとデータ抽出ルール
└── AndroidManifest.xml         # アプリマニフェストと権限

src/test/                       # ユニットテスト（JVM）
└── java/com/example/myapplication/
    └── ExampleUnitTest.kt      # サンプルユニットテスト

src/androidTest/               # インストルメンテーションテスト（Androidデバイス）
└── java/com/example/myapplication/
    └── ExampleInstrumentedTest.kt  # サンプルインストルメンテーションテスト
```