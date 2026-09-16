# GitHub Actions CI/CD for MannSaathi (मनसाथી)

This directory contains the automated GitHub Actions workflow for building and distributing the **MannSaathi** Android APK.

## Workflow Overview (`build-and-release.yml`)

The workflow automatically:
1. Sets up JDK 17 and configures Gradle caching.
2. Initializes `.env` (copying from `.env.example` if needed).
3. Ensures a valid `debug.keystore` is present (decoding from `debug.keystore.base64` or generating a fresh standard Android debug keystore).
4. Compiles and packages the **Debug APK** (`MannSaathi-debug.apk`).
5. Checks if production release signing secrets exist:
   - If secrets are present: Builds and signs the **Release APK** (`MannSaathi-release.apk`).
   - If secrets are absent: Gracefully proceeds with the debug APK.
6. Uploads the built APKs as downloadable workflow artifacts (`MannSaathi-APKs`).
7. When triggered by a git release tag (e.g. `v1.0.0`), automatically publishes a **GitHub Release** with the APKs attached.

---

## Configuring Optional Release Secrets

If you want GitHub Actions to compile and sign production release APKs, add the following secrets in your repository:
**GitHub Repository → Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | Description | Example / Instructions |
| :--- | :--- | :--- |
| `KEYSTORE_BASE64` | Base64-encoded string of your `.jks` or `.keystore` file | Run `base64 -w 0 my-upload-key.jks` and paste the output |
| `STORE_PASSWORD` | Password for your release keystore | Your keystore password |
| `KEY_PASSWORD` | Password for your release private key | Your key password |
| `GEMINI_API_KEY` | *(Optional)* Google Gemini API Key | Provided via AI Studio or Google AI Developer Portal |

---

## Manual Trigger

You can manually trigger an APK build at any time:
1. Navigate to the **Actions** tab on your GitHub repository.
2. Select **Build and Release MannSaathi APK** in the left sidebar.
3. Click the **Run workflow** dropdown and confirm on branch `main` (or `master`).
4. Wait ~2-3 minutes for the build to complete, then download the APK under **Artifacts**.
