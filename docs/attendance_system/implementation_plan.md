# 実装計画: CI/CD (GitHub Actions)

## 目標
GitHub Actionsを使用して、以下の自動化ワークフローを構築する。
1. **Webアプリ**: ビルド、テスト、Firebase Hostingへのデプロイ
2. **Androidアプリ**: ビルド (APK生成)、ユニットテスト実行

## ユーザー対応手順 (重要)
これらのワークフローを機能させるには、ユーザー側で以下の操作が必要になります。
1. **GitHubリポジトリの作成とプッシュ**: 現在のコードをGitHubにプッシュする。
2. **Secretsの設定**: リポジトリの `Settings > Secrets and variables > Actions` に以下を追加する。
    - `FIREBASE_SERVICE_ACCOUNT`: Firebaseのサービスアカウントキー (JSON)
    - `FIREBASE_API_KEY`, `FIREBASE_PROJECT_ID` 等: Webアプリの環境変数用 (必要に応じて)

## 変更内容

### ワークフローファイル (`.github/workflows/`)

#### [NEW] [.github/workflows/web-deploy.yml](file:///c:/Users/owner/Desktop/project-android/.github/workflows/web-deploy.yml)
- トリガー: `main` ブランチへのプッシュ (`web/` 配下の変更時)
- ジョブ: `build-and-deploy`
    - Node.js セットアップ
    - 依存関係インストール (`npm ci`)
    - ビルド (`npm run build`)
    - Firebase Deploy (`firebase-tools` 使用)

#### [NEW] [.github/workflows/android-build.yml](file:///c:/Users/owner/Desktop/project-android/.github/workflows/android-build.yml)
- トリガー: 全ブランチへのプッシュ (`android/` 配下の変更時)
- ジョブ: `build-debug`
    - JDK セットアップ (Java 17)
    - Android SDK キャッシュ
    - ビルド (`./gradlew assembleDebug`)
    - テスト (`./gradlew testDebugUnitTest`)
    - Artifact保存 (APKファイルを成果物としてアップロード)

## 構成のポイント
- **Monorepo対応**: `paths` フィルタを使用して、WebとAndroidで変更があった場合のみそれぞれのワークフローが動くようにする。
- **キャッシュ活用**: `gradle` や `npm` のキャッシュを利用してビルド時間を短縮する。

## 検証計画
- **手動確認**: ワークフローファイルを作成後、ユーザーがGitHubにプッシュし、Actionsタブでビルドが成功することを確認する。
