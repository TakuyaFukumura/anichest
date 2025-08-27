# プロジェクト固有の ProGuard ルールをここに追加してください。
# build.gradle の proguardFiles 設定で適用される設定ファイルのセットを制御できます。
#
# 詳細については以下を参照してください。
#   http://developer.android.com/guide/developing/tools/proguard.html

# プロジェクトで WebView と JS を使用する場合は、以下をアンコメントし、
# JavaScript インターフェースクラスの完全修飾クラス名を指定してください:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# スタックトレースのデバッグ用に行番号情報を保持する場合は、これをアンコメントしてください。
#-keepattributes SourceFile,LineNumberTable

# 行番号情報を保持する場合、元のソースファイル名を隠すにはこれをアンコメントしてください。
#-renamesourcefileattribute SourceFile
