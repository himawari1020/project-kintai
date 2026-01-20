# 検証手順: CI/CD (GitHub Actions)

## 1. GitHubリポジトリの準備
1. GitHubで新しいリポジトリを作成する。
2. ローカルのリポジトリをリモートに追加し、プッシュする。
   ```bash
   git remote add origin <REPOSITORY_URL>
   git branch -M main
   git push -u origin main
   ```

## 2. Secretsの設定
Webアプリの自動デプロイを有効にするため、以下のSecretをGitHubリポジトリに設定する。
- **設定場所**: Settings > Secrets and variables > Actions > New repository secret
- **名前**: `FIREBASE_SERVICE_ACCOUNT`
- **値**: Firebaseコンソールからダウンロードしたサービスアカウントキー (JSONファイルの中身すべて)

## 3. 動作確認
- GitHubの **Actions** タブを開く。
- `Web App Deploy` ワークフローが成功し、Firebase Hostingに変更が反映されていることを確認する。
- `Android Build` ワークフローが成功し、ArtifactsからAPKがダウンロードできることを確認する。
