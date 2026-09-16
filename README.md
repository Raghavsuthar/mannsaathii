# MannSaathi (मनसाथી) — Dementia & Elder Care Companion

**MannSaathi (मनसाथી)** is a compassionate, bilingual (English, Gujarati, Hindi) Android application built with Jetpack Compose and Material Design 3. Designed specifically for dementia and Alzheimer's patients and their family caregivers, it features routine orientation, medication reminders, memory books with photo capture and audio narration, calming audio soundscapes, and emergency assistance.

---

## 📱 Download APK from GitHub

You can easily download and install the APK directly onto any Android device using either of the two methods below:

### Option A — Download from GitHub Actions (Fastest & Easiest)

Whenever code is pushed to the repository or triggered manually, GitHub Actions automatically builds the APK.

1. Go to your repository on GitHub.
2. Click on the **Actions** tab at the top.
3. In the workflow list, click on the latest run titled **"Build and Release MannSaathi APK"** (or the latest commit).
4. Scroll down to the **Artifacts** section at the bottom of the run summary.
5. Click **`MannSaathi-APKs`** to download the zip file.
6. Extract the zip to find **`MannSaathi-debug.apk`**.
7. Transfer to your Android phone or open directly on device to install.

> *Note: Android may display a standard prompt: "Install unknown apps". Tap "Settings" and toggle "Allow from this source" to complete the installation.*

---

### Option B — Download from GitHub Releases

For tagged releases (e.g. `v1.0.0`), pre-built APKs are attached directly to the release page:

1. Go to the main page of the repository.
2. In the right-hand sidebar, click on **Releases** (or navigate to `https://github.com/<your-username>/<your-repo>/releases`).
3. Under **Assets**, click **`MannSaathi-debug.apk`** (or `MannSaathi-release.apk`) to download it directly.

---

## 🚀 How to Push this Project to GitHub

Follow these simple steps in your terminal to create and push this repository:

```bash
# 1. Initialize git (if not already done)
git init

# 2. Add all files
git add .

# 3. Commit the code
git commit -m "Initial commit: MannSaathi with automated GitHub Actions CI/CD"

# 4. Rename branch to main
git branch -M main

# 5. Connect to your GitHub repository (replace with your repo URL)
git remote add origin https://github.com/<your-username>/<your-repo-name>.git

# 6. Push code to GitHub
git push -u origin main
```

Once pushed, GitHub Actions will automatically start building the APK.

---

## 🏷️ How to Create a New Release Tag

To trigger an official GitHub Release with downloadable APK assets attached:

```bash
# Create a semantic version tag
git tag v1.0.0

# Push the tag to GitHub
git push origin v1.0.0
```

GitHub Actions will automatically build the APK and publish a release under the **Releases** tab.

---

## 🔐 Optional: Setting Up Signed Production Releases

By default, the workflow produces a fully functional **Debug APK** that installs immediately on any Android device without extra configuration.

If you wish to produce a cryptographically signed **Release APK** for production or Play Store distribution:

1. Generate your release keystore locally:
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Convert the keystore file to base64:
   - **Linux / macOS**: `base64 -w 0 my-upload-key.jks` (or `base64 -i my-upload-key.jks`)
   - **Windows PowerShell**: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("my-upload-key.jks"))`
3. In your GitHub repository, go to **Settings → Secrets and variables → Actions → New repository secret**.
4. Add the following secrets:
   - `KEYSTORE_BASE64`: The full base64 string of your keystore file
   - `STORE_PASSWORD`: Keystore password
   - `KEY_PASSWORD`: Key alias password
   - `GEMINI_API_KEY`: *(Optional)* Your Gemini API Key from Google AI Studio

When these secrets are present, GitHub Actions will automatically build both **`MannSaathi-debug.apk`** and **`MannSaathi-release.apk`**.

---

## 💻 Local Development & Building in Android Studio

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Android SDK 36 (compileSdk 36, minSdk 24)

### Building via Gradle CLI
```bash
# Build debug APK
./gradlew :app:assembleDebug

# Run unit tests
./gradlew :app:testDebugUnitTest
```

---

## 🛡️ Security Hardening & Reverse-Engineering Protections

MannSaathi includes multi-layered code and runtime hardening designed to protect patient privacy and intellectual property from decompilation, cloning, and tampering:

1. **R8 / ProGuard Code Obfuscation & Shrinking (`isMinifyEnabled = true`, `isShrinkResources = true`)**:
   - Release builds undergo full bytecode shrinking, dead-code elimination, and identifier renaming.
   - Classes, variables, and internal method names are shortened to cryptic single-letter tokens (`a`, `b`, `c`), making decompilation tools (JADX, APKTool, CFR) output nearly unreadable logic.
   - Resource shrinking removes unused strings, layouts, and assets from the final release APK.
   - Debug log statements (`android.util.Log.d`, `Log.v`) are stripped from release bytecode to prevent leaking sensitive patient health logs.
   - Line number tables are preserved (`SourceFile,LineNumberTable`) while hiding real Kotlin filenames, enabling actionable crash stacktraces without exposing developer directory structures.

2. **Manifest Hardening**:
   - `android:allowBackup="false"`: Disables ADB and cloud backup vectors, preventing unauthorized extraction of private SQLite Room databases or cached patient photos without root.
   - `android:installLocation="internalOnly"`: Prevents installing or moving the application to external SD cards where APKs or private files could be tampered with.
   - `android:usesCleartextTraffic="false"`: Strictly enforces encrypted HTTPS transport across all network requests.
   - `android:extractNativeLibs="false"`: Keeps native binaries packaged inside the APK.

3. **Lightweight Runtime Integrity Checks (`SecurityHelper.kt`)**:
   - Checks for signs of rooted environments (e.g. `/system/bin/su`, `/data/local/su`, `test-keys` build signatures).
   - Detects attached debuggers (`Debug.isDebuggerConnected()`).
   - Diagnoses emulator execution environments without obstructing legitimate accessibility or virtualized previews.
   - Non-disruptive design: Avoids false-positive lockouts to guarantee uninterrupted care for elderly patients.

4. **Security Limitations**:
   - *Note on Client-Side Security*: 100% protection against determined reverse engineers on a client device is technically impossible. However, these industry-standard measures dramatically raise the reverse-engineering cost and barrier of entry, rendering automated cloning and low-effort decompilation ineffective.

---

## 🛠️ Google AI Studio Notes
- Built using **Jetpack Compose**, **Material Design 3**, **Room Database**, **Navigation Compose**, and **TTS**.
- Secrets and API keys are managed through the Secrets panel in AI Studio via `.env` / `.env.example`.
- Pre-configured for both Google AI Studio streaming emulator deployment and standard GitHub CI/CD workflows.
