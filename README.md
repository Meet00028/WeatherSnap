# WeatherSnap

An Android (Kotlin) app that fetches live weather from **Open‑Meteo**, lets you capture a **CameraX** photo as evidence, compresses it, and stores reports locally in **Room**.

## Tech stack
- Kotlin + Coroutines/Flow
- Jetpack Compose (Material 3)
- MVVM + StateFlow + ViewModel
- Hilt (DI)
- Navigation Compose
- Retrofit + Gson + OkHttp (debug-only logging)
- Room Database
- CameraX (no camera intent)

## Setup
1. Open the project folder `WeatherSnap/` in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator/device (Android 7.0+).
4. Grant **Camera** permission when prompted on the camera screen.

If you run Gradle from the terminal on macOS/Linux, you may need:
```bash
chmod +x ./gradlew
```

## Notes
- Weather is fetched from Open‑Meteo (no API key).
- Reports are stored locally in Room (no cloud sync).
