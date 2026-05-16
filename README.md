# WeatherSnap

An Android app that fetches live weather from Open-Meteo, captures CameraX photos as evidence, compresses them, and stores reports locally in Room DB.

## Tech Stack
- Kotlin + Coroutines/Flow
- Jetpack Compose (Material 3)
- MVVM + StateFlow + ViewModel
- Hilt (DI)
- Navigation Compose with slide transitions
- Retrofit + Gson + OkHttp (debug-only logging)
- Room Database (IO thread enforced)
- CameraX (no camera intent)
- DataStore (offline weather cache)

## Setup & Run

1. Clone: `git clone https://github.com/Meet00028/WeatherSnap.git`
2. Open `WeatherSnap/` in Android Studio (Hedgehog 2023.1.1 or newer)
3. Let Gradle sync complete automatically
4. Run on emulator (API 34+) or device (Android 7.0+) — click Run ▶
5. Grant **Camera** permission when prompted
6. No API key required — uses Open-Meteo (free, no auth)

## App Flow

1. Search a city → autocomplete suggestions after 2+ letters
2. Select suggestion → live weather loads (temp, condition, humidity, wind, pressure)
3. Tap **Create Report** → custom CameraX screen opens
4. Capture photo → compressed automatically → original vs compressed sizes shown
5. Add field notes → tap **Save Report** → persisted in Room DB
6. Tap **Reports** → view all saved reports with image, weather, sizes, notes, timestamp

## Bonus Features Implemented
- ✅ Unit tests — WeatherRepository + WeatherViewModel (MockK + Turbine)
- ✅ Compose UI tests — WeatherScreen + ReportsScreen
- ✅ Offline fallback with DataStore cache + offline banner
- ✅ Debug-only network logging (BuildConfig.DEBUG)

## Notes
- No splash screen, login, or onboarding
- No hardcoded mock data — all weather is live
- CameraX only — no ACTION_IMAGE_CAPTURE intent
- Reports always persisted to Room DB, never in-memory only
