# WeatherSnap

An Android app that fetches live weather from Open-Meteo, captures CameraX photos as evidence, compresses them, and stores reports locally in Room DB.

## Tech Stack
- Kotlin + Coroutines/Flow
- Jetpack Compose (Material 3)
- MVVM + StateFlow + ViewModel
- Hilt (DI)
- Navigation Compose
- Retrofit + Gson + OkHttp (debug-only logging)
- Room Database
- CameraX (no camera intent)
- DataStore (offline cache)

## Setup & Run

1. Clone the repository:
   git clone https://github.com/YOUR_USERNAME/WeatherSnap.git

2. Open the WeatherSnap/ folder in Android Studio (Hedgehog or newer).

3. Let Gradle sync complete automatically.

4. Run on an emulator (API 34+) or physical device (Android 7.0+):
   - Click the Run button in Android Studio
   - Grant Camera permission when prompted on the camera screen

5. No API key required — uses Open-Meteo (free, no auth).

## App Flow

1. Search a city → autocomplete suggestions appear after 2+ letters
2. Select a city → live weather loads (temperature, condition, humidity, wind, pressure)
3. Tap Create Report → capture a photo using the custom CameraX screen
4. Image is compressed automatically → original and compressed sizes shown
5. Add field notes → Save Report → stored in Room DB
6. View all saved reports in the Saved Reports screen

## Features
- City suggestion caching (no repeated API calls)
- Offline fallback with cached weather data
- Image compression (JPEG 60% quality)
- All DB operations on IO thread
- Slide navigation transitions
- Animated state changes and city suggestions
- Unit tests (Repository + ViewModel)
- Compose UI tests
- Debug-only network logging

## Notes
- No splash screen, login, or onboarding
- No mock data — all weather is live from Open-Meteo
- Camera uses CameraX only, no device camera intent
- Reports persist in Room DB, not just in memory
