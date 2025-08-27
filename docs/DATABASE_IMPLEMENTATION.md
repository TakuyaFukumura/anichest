# データベース統合の実装詳細

## 概要
本実装では、Roomライブラリを使用してSQLiteデータベースを統合し、"Android"という文字列をデータベースから取得して画面に表示します。

## アーキテクチャ

### MVVM + Repository パターン
```
View (MainActivity/Compose) 
    ↓
ViewModel (MainViewModel)
    ↓
Repository (StringRepository)
    ↓
DAO (StringDao)
    ↓
Database (Room/SQLite)
```

## 実装詳細

### 1. データベースエンティティ
- **ファイル**: `StringEntity.kt`
- **目的**: データベーステーブルの定義
- **内容**: IDと文字列値を持つシンプルなエンティティ

```kotlin
@Entity(tableName = "strings")
data class StringEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String
)
```

### 2. DAO (Data Access Object)
- **ファイル**: `StringDao.kt`
- **目的**: データベースアクセスメソッドの定義
- **機能**:
  - 文字列の取得
  - 文字列の挿入
  - データの削除

### 3. データベース設定
- **ファイル**: `AppDatabase.kt`
- **目的**: Roomデータベースの設定と初期化
- **特徴**:
  - シングルトンパターンで実装
  - データベース作成時に"Android"文字列を自動挿入

### 4. リポジトリ
- **ファイル**: `StringRepository.kt`
- **目的**: データアクセスの抽象化
- **利点**:
  - ViewModelとDAOの疎結合
  - テスタビリティの向上

### 5. ViewModel
- **ファイル**: `MainViewModel.kt`
- **目的**: UI状態の管理とビジネスロジック
- **機能**:
  - データベースからの非同期データ取得
  - StateFlowによるリアクティブな状態管理
  - エラーハンドリング

### 6. アプリケーションクラス
- **ファイル**: `MyApplication.kt`
- **目的**: アプリケーション全体の初期化
- **機能**:
  - データベースインスタンスの作成
  - リポジトリの初期化

## データフロー

1. **アプリ起動時**:
   - `MyApplication`でデータベースとリポジトリを初期化
   - データベースコールバックで"Android"文字列を挿入

2. **画面表示時**:
   - `MainActivity`で`MainViewModel`を作成
   - `MainViewModel`が`StringRepository`経由でデータを取得
   - StateFlowでUI状態を更新
   - Composeでリアクティブに画面を描画

## 非同期処理
- **Kotlin Coroutines**を使用した非同期データベースアクセス
- **StateFlow**による状態管理でUIスレッドの安全性を確保
- **ViewModel Scope**でライフサイクルに応じた適切なキャンセル

## エラーハンドリング
- データベースアクセス失敗時は"Android"をデフォルト値として表示
- try-catch文による例外処理
- ユーザーエクスペリエンスを損なわない設計

## テスト
- **ユニットテスト**: エンティティクラスの基本機能をテスト
- **インストルメンテーションテスト**: アプリケーションクラスの動作確認

## 依存関係の追加
```gradle
// Room dependencies
implementation("androidx.room:room-runtime:2.5.0")
implementation("androidx.room:room-ktx:2.5.0")
kapt("androidx.room:room-compiler:2.5.0")

// ViewModel and Compose integration
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
```

## 利点
1. **データの永続化**: アプリ再起動後もデータが保持される
2. **型安全性**: Roomによるコンパイル時チェック
3. **パフォーマンス**: SQLiteの最適化されたアクセス
4. **保守性**: アーキテクチャによる責任の分離
5. **テスタビリティ**: 各層の独立したテスト可能性
