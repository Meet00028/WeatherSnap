# WeatherSnap

Android app for live weather reports with CameraX photo evidence, built with Kotlin + Jetpack Compose.

## Tech Stack
Kotlin, Jetpack Compose, MVVM, Hilt, Room, CameraX, Retrofit, Navigation Compose, Material 3

## Setup & Run

1. Clone: `git clone `https://github.com/Meet00028/WeatherSnap.git``
2. Open `WeatherSnap/` in Android Studio
3. Let Gradle sync complete
4. Run on emulator (API 34+) or device (Android 7.0+)
5. Grant Camera permission when prompted

No API key required — uses Open-Meteo (free).

## Notes
- No mock data — all weather is live
- CameraX only, no camera intent
- Reports saved in Room DB
