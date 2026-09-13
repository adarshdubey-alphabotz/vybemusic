<div align="center">
  <h1>🎵 Vybe Music</h1>
  <p><b>Stream Without Limits — Apple Music Aesthetics • YouTube Music Discovery • Spotify Jam Rooms</b></p>

  <p>
    <img src="https://img.shields.io/badge/Platform-Android_Native-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
    <img src="https://img.shields.io/badge/Language-Kotlin_1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
    <img src="https://img.shields.io/badge/Audio-Media3_ExoPlayer_(320kbps)-FF0055?style=for-the-badge&logo=applemusic&logoColor=white" alt="Audio" />
    <img src="https://img.shields.io/badge/Play_Store-Ready-34A853?style=for-the-badge&logo=googleplay&logoColor=white" alt="Play Store Ready" />
  </p>

  <p>
    <a href="#-features">Features</a> •
    <a href="#-architecture">Architecture</a> •
    <a href="#-building-and-running">Build & Run</a> •
    <a href="#-spotify-jam-collaborative-listening">Vybe Jam</a> •
    <a href="#-play-store-launch-guide">Play Store Guide</a>
  </p>
</div>

---

## ✨ Features

### 🍎 1. Apple Music Tier Polish & Aesthetics
* **Fluid Ambient Mesh Background:** The entire player dynamically extracts the dominant & accent colors from album artwork and renders a moving ambient glow.
* **Lossless 320kbps Studio Audio:** Streams in pure 320kbps AAC audio with a dedicated "LOSSLESS" badge indicator.
* **Karaoke Synced Lyrics (LRCLIB):** Real-time line-by-line synced lyrics that auto-scroll and glow as the artist sings. Tap any line to seek instantly.
* **3D Elevating Album Card:** High-resolution album artwork with colored drop shadows that respond to player state.

### 🔴 2. YouTube Music Discovery & Navigation
* **Intuitive Layout:** Tabs for **Home**, **Explore**, and **Search**.
* **Quick Picks Carousel:** Instant radios and top trending songs across English, Hindi, Punjabi, Spanish, and 15+ languages.
* **Floating Bottom Mini-Player:** Smooth persistent player pill with top progress line and skip controls that expands into the full player on click.
* **Instant Real-Time Search:** Fast debounced search across millions of tracks, albums, and artists.

### 👥 3. Spotify Jam Real-Time Collaborative Sessions ("Vybe Jam")
* **Host a Session:** Generate a 6-character room code (e.g. `VYBE-8429`) with one tap.
* **Listen in Sync:** All connected friends hear the exact same song at the exact same millisecond. Host pausing or seeking syncs across all devices.
* **Collaborative Queue:** Friends can upvote songs and add their favorite tracks into the live shared queue.

---

## 🏛️ Architecture

```
vybemusic/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── java/com/alphabotz/vybemusic/
│   │       ├── MainActivity.kt
│   │       ├── core/
│   │       │   ├── model/         # Track, Lyrics, JamSession, Playlist
│   │       │   ├── network/       # VybeMusicEngine (320kbps), LrclibLyricsApi, VybeJamEngine
│   │       │   └── playback/      # VybePlayerController, VybePlaybackService (Media3)
│   │       └── ui/
│   │           ├── theme/         # OLED Dark Material 3 Palette
│   │           ├── components/    # AppleMusicPlayer, KaraokeLyricsView, VybeMiniPlayer, VybeJamSheet
│   │           └── screens/       # MainScaffold, HomeScreen, SearchScreen, ExploreScreen
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml         # Version catalog
├── settings.gradle.kts
└── build.gradle.kts
```

---

## 🛠️ Building and Running

### Prerequisites:
* Android Studio Iguana / Jellyfish (or newer)
* JDK 17
* Android SDK 34 (Android 14+)

### Build APK:
```bash
# Clone the repository
git clone https://github.com/adarshdubey-alphabotz/vybemusic.git
cd vybemusic

# Build Debug APK
./gradlew assembleDebug

# Build Play Store Release Bundle (.aab)
./gradlew bundleRelease
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🚀 Play Store Launch Guide

1. **Package ID:** `com.alphabotz.vybemusic`
2. **App Icons & Banners:** Place launcher icons in `res/mipmap-xxxhdpi/`
3. **Generate Keystore:**
   ```bash
   keytool -genkey -v -keystore vybe-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias vybemusic
   ```
4. **Google Play Console:**
   * Select **Music & Audio** category.
   * Provide the compliant metadata disclaimer: *"Vybe Music aggregates publicly available music feeds via official streaming endpoints."*
   * Upload `app-release.aab` to Production or Closed Testing track.

---

## 📄 License & Credits
* Developed by **[Adarsh Dubey](https://github.com/adarshdubey-alphabotz)** (Founder @ Alphabotz).
* Open-sourced under the **MIT License**.
