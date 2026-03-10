# MusicShelf 🎵

MusicShelf is a modern, premium Android application for managing and curating personalized track playlists. Designed with a dark, minimal aesthetic, it leverages Jetpack Compose and modern Android architecture to deliver a smooth, responsive, and delightful user experience.

## ✨ Features

- **Playlist Management**: Create, view, and manage custom playlists.
- **Track Organization**: Add tracks with rich details (Title, Artist, Album, Duration) and reorder them seamlessly.
- **Advanced Sorting**: Sort your tracks by Position, BPM, Duration, or Date Added.
- **Mood Filters**: Filter your playlists on the Home Screen using custom mood chips with bouncy spring animations.
- **Swipe-to-Delete with Undo**: Easily remove tracks with a swipe gesture and temporarily restore them via Snackbar.
- **Premium Dark UI**: A pure dark theme featuring custom Canvas-drawn empty state illustrations (vinyl records, track lists) and dynamic edge-to-edge screens.
- **Persistent Preferences**: Saves your default theme, mood filters, and sort orders safely via Proto DataStore.
- **Spotify Integration**: Connect your Spotify account to import your favorite playlists directly into MusicShelf.

## 🛠 Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Clean Architecture principles
- **Dependency Injection**: Dagger Hilt
- **Local Storage**: Room Database (with Flow integration)
- **Preferences**: Proto DataStore
- **Navigation**: Navigation Compose with custom layout transitions
- **Image Loading**: Coil

## 📥 Download APK

You can download and install the latest version of the MusicShelf app directly to your Android device.

**[Download MusicShelf APK](./MusicShelf.apk)**

> **Note:** Remember to allow installation from "Unknown Sources" in your Android settings to install the debug APK.

## 🚀 Getting Started locally

1. Clone this repository.
2. Open the project in **Android Studio**.
3. Let Gradle sync project dependencies.
4. Run the `app` module on an emulator or physical device.


---
*Built with ❤️ utilizing modern Android development practices.*
