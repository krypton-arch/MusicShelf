# MusicShelf Project Documentation

## 1. Project Overview
**MusicShelf** is a premium, offline-first Android application designed for curating, managing, and arranging personalized music playlists. Boasting a striking dark and minimal aesthetic accented by deep purples and dynamic interactions, the application provides an immersive experience out of the box. 

Currently, the application has completed its core phases, offering fully functional offline playlist curation using modern Jetpack Compose UIs, Room database, and Proto DataStore-backed user preferences.

## 2. Technical Stack and Architecture
The project strictly implements a **Clean Architecture (MVVM)** pattern, isolating concerns across Data, Domain, and UI layers.

### Core Technologies
- **UI Framework:** Jetpack Compose (Material 3) with custom spring physics and Canvas illustrations.
- **Dependency Injection:** Dagger Hilt
- **Local Storage:** Room Database (Reactive flows via DAOs)
- **Settings/Preferences:** Jetpack Proto DataStore (Protobuf)
- **Navigation:** Navigation Compose
- **Image Loading:** Coil 3
- **Kotlin Features:** Coroutines, StateFlows, KSP

### Layer Definitions
- **UI (Presentation):** Composables and ViewModels. Uses `StateFlow` and `collectAsStateWithLifecycle()` to achieve reactive UI updates.
- **Domain:** Repository interfaces mapping pure Kotlin business logic (`domain/repository`).
- **Data:** Repository implementations (`data/repository`), Room DAOs, DataStore serializers mapping to local SQLite and Protobuf data.

## 3. Database Schema
MusicShelf relies on relational models persisted via Room SQLite.
1. **PlaylistEntity:** Holds playlist metadata (UUID primary key, name, description, cover URI, mood tag, and timestamps).
2. **TrackEntity:** Holds individual track metadata (Title, artist, duration, local/Spotify URIs). Uses a ForeignKey constraint mapping `playlistId` with a `CASCADE` delete policy to ensure database integrity if a parent playlist is removed. It also holds an ascending `position` integer for ordering.
3. **MoodTagEntity:** Seeded lookup table defining predefined mood tags, hex colors, and emojis.

## 4. Implemented Features (Completed Phases)

### Phase 1: Local Core
- **Database Schema and Setup:** Room DB seamlessly providing Flows for realtime updates.
- **Home Screen:** Staggered grid view of Playlist cards with empty state Canvas illustrations (vinyl record).
- **Playlist Management Form:** Creation of playlists supporting customizable names and pre-seeded Mood Tags.
- **Playlist Details:** Deep dive into tracklists with custom swappable list canvas empty states. Tracks render reactively as `TrackItem`s.
- **Track Operations:** Addition of tracks, swipe-to-delete track interaction featuring 4-second Snackbar Undo restore capability natively integrated with Repository logic.

### Phase 2: Smart Filtering & Preferences
- **Proto DataStore Integration:** Settings serialization via Protobuf eliminating key typos found in SharedPreferences mapping.
- **Settings Screen:** User modification of Theme configuration, Default Sort configuration, and Default Mood configuring.
- **Reactive UI Sorting:** Playlist Details allows for real-time Sorting overriding via a DropdownMenu (Options: Position, BPM, Duration, Date Added).
- **Bouncy Animations:** Real-time staggered filter chips inside of `HomeScreen` utilizing `AnimatedVisibility` and Spring physics.

## 5. Upcoming Features (Roadmap)
Future development on the MusicShelf agent will push the offline manager into a cloud-integrated platform.

- **Phase 3 - Spotify Integration:** OAuth 2.0 PKCE authentication flow via Chrome Custom tabs to fetch Spotify libraries and push customized `MusicShelf` curation back up to the user's Spotify Account via Retrofit.
- **Phase 4 - Collaborative Cloud:** Migrating local playlists dynamically to Firebase Firestore. Creation of shareable Deep Links for real-time collaborative tracklisting via Firebase Auth and Realtime listeners.
- **Phase 5 - AI Suggestions:** Deep integration with Gemini AI API. Users submit natural language prompts ("lo-fi beats for studying") and Gemini outputs a JSON block mapping to `TrackEntity` lists for effortless intelligent additions. 

## 6. How to Build & Run
1. Open the source directory folder in Android Studio Ladybug (or higher). 
2. Let `.gradle` Sync finish resolving KSP and Jetpack Compose dependencies.
3. Build the `app` module target on any simulator or physical device running API 26 or higher.

*Built iteratively as a portfolio piece showcasing Tier-1 Android practices.*
