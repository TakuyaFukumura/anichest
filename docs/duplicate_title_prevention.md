# タイトル重複登録防止機能

## 概要

バージョン 4.7.0 にて、アニメタイトルの重複登録を防ぐ機能を実装しました。

## 実装内容

### 1. データベース制約

```kotlin
@Entity(
    tableName = "anime",
    indices = [Index(value = ["title"], unique = true)]
)
data class Anime(...)
```

- `title` フィールドにユニークインデックスを追加
- データベースレベルでタイトルの一意性を保証

### 2. バリデーション機能

#### 新規メソッド: `insertAnimeWithValidation()`

```kotlin
suspend fun insertAnimeWithValidation(anime: Anime): Long {
    // タイトルバリデーション
    if (anime.title.isBlank()) {
        throw InvalidTitleException("アニメタイトルは必須です")
    }
    
    // 重複チェック
    if (animeDao.existsByTitle(anime.title.trim())) {
        throw DuplicateTitleException("「${anime.title.trim()}」は既に登録されています")
    }
    
    // トリムされたタイトルで挿入
    val trimmedAnime = anime.copy(title = anime.title.trim())
    return animeDao.insertAnime(trimmedAnime)
}
```

#### 例外クラス

- `DuplicateTitleException`: タイトル重複時にスロー
- `InvalidTitleException`: 無効なタイトル時にスロー

### 3. 使用方法

#### 通常の登録（重複チェックなし）
```kotlin
val animeId = repository.insertAnime(anime)
```

#### バリデーション付き登録（推奨）
```kotlin
try {
    val animeId = repository.insertAnimeWithValidation(anime)
    // 成功処理
} catch (e: DuplicateTitleException) {
    // 重複エラー処理
} catch (e: InvalidTitleException) {
    // バリデーションエラー処理
}
```

### 4. CSVインポート機能

CSVインポート時は自動的に重複チェックが行われ、重複したタイトルはスキップされます。

```kotlin
val result = repository.importFromCsv(context, uri)
println("成功: ${result.successCount}, スキップ: ${result.skipCount}")
```

### 5. 前方互換性

既存の `insertAnime()` メソッドは引き続き利用可能ですが、データベース制約により重複タイトルの挿入時には例外がスローされます。

新規開発では `insertAnimeWithValidation()` の使用を推奨します。

## テスト

重複チェック機能の包括的なテストは `AnimeRepositoryDuplicateTest` クラスで実装されています。

- 正常なタイトル登録のテスト
- 空/ブランクタイトルのバリデーションテスト
- 重複タイトルのチェックテスト
- 前後の空白文字のトリム処理テスト

## マイグレーション

既存のデータベースでは、データベーススキーマの変更により既存の重複データがある場合にはマイグレーションエラーが発生する可能性があります。

現在の実装では `fallbackToDestructiveMigration(true)` が設定されているため、スキーマ変更時にはデータベースが再作成されます。
