# 🎵 MusicShelf — Music Playlist Manager
## Agent Specification File v1.0

---

## Project Identity

**App Name:** MusicShelf  
**Platform:** Android (API 26+)  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Architecture:** Clean Architecture — MVVM (UI → ViewModel → UseCase → Repository → Room/Firebase)  
**Target:** Production-ready, portfolio-grade Android application  

---

## Documentation Directive (CRITICAL — Follow Every Step)

After completing **every phase or sub-task**, the agent MUST update a file called `AI_DEV_LOG.md` in the project root.

### `AI_DEV_LOG.md` format per entry:

```markdown
## [Phase X.Y] — <Task Title>
**Date Completed:** <auto or placeholder>
**Status:** ✅ Done | 🔄 In Progress | ❌ Blocked

### What Was Built
- Bullet list of files created or modified
- Key classes, composables, or functions added

### Architecture Decisions
- Why this approach was chosen over alternatives

### Known Issues / TODOs
- Any deferred logic, edge cases, or hardcoded values to revisit

### How to Test This Step
- Manual test steps or unit test commands

***
```

This log is the single source of truth for the project's development history. Never skip updating it.

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose, Material 3 |
| State Management | ViewModel + StateFlow + collectAsStateWithLifecycle() |
| Local Database | Room (Playlists, Tracks, MoodTags) |
| Preferences | Jetpack DataStore (Proto) |
| Remote Sync | Firebase Firestore |
| Auth | Firebase Auth + Spotify OAuth 2.0 (PKCE) |
| Networking | Retrofit 2 + OkHttp + Gson |
| Image Loading | Coil 3 |
| DI | Hilt |
| Navigation | Jetpack Navigation Compose |
| Background | WorkManager |
| AI Features | Gemini API (via REST) |
| Testing | JUnit 4, MockK, Compose UI Testing |

---

## Design System

- **Background:** `#0A0A0F`
- **Surface:** `#12121A`
- **Primary Accent:** `#7C5CBF` (purple — music/creative)
- **Secondary Accent:** `#1DB954` (Spotify green — for integration cues)
- **Text Primary:** `#FFFFFF`
- **Text Secondary:** `#A0A0B0`
- **Font:** Inter (via Google Fonts in Compose)
- **Corner Radius:** 16dp cards, 24dp bottom sheets
- **Animations:** Spring-based transitions, shared element transitions for playlist → detail, drag-to-reorder haptic feedback
- **NO tacky gradients, NO neon overload** — dark, minimal, premium aesthetic

---

## Database Schema (Room)

```kotlin
@Entity data class PlaylistEntity(
    @PrimaryKey val id: String,         // UUID
    val name: String,
    val description: String,
    val coverUri: String?,
    val moodTag: String,                // "chill", "hype", "focus", "sad", "party"
    val isCollaborative: Boolean,
    val spotifyId: String?,             // null if local-only
    val createdAt: Long,
    val updatedAt: Long
)

@Entity data class TrackEntity(
    @PrimaryKey val id: String,
    val playlistId: String,             // FK → PlaylistEntity
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val bpm: Int?,
    val coverUri: String?,
    val spotifyUri: String?,
    val localUri: String?,              // for offline files
    val position: Int,
    val addedAt: Long
)

@Entity data class MoodTagEntity(
    @PrimaryKey val tag: String,
    val color: String,                  // hex
    val emoji: String
)
```

---

## Navigation Graph

```
NavHost
├── HomeScreen (playlist grid, FAB to create)
├── PlaylistDetailScreen (track list, reorder, mood filter)
├── AddTrackScreen (search Spotify or local)
├── CreatePlaylistScreen (name, mood, cover art)
├── SpotifyImportScreen (OAuth login → playlist picker)
├── CollabShareScreen (Firebase share link)
├── SettingsScreen (theme, DataStore prefs)
├── AiSuggestScreen (vibe prompt → AI tracklist)
└── OnboardingScreen (first launch)
```

---

## Phase 1 — Local Core

**Goal:** Fully functional offline playlist manager with Room + Compose.

### Tasks:
1. Set up project with Hilt, Room, Navigation Compose, Material 3 theme
2. Implement Room schema: `PlaylistEntity`, `TrackEntity`, `MoodTagEntity`
3. Build `PlaylistRepository` + `PlaylistDao`
4. Create `HomeScreen` — grid of playlist cards with cover art (Coil), mood badge, track count
5. Create `CreatePlaylistScreen` — name input, mood tag picker (chip group), optional cover image picker
6. Create `PlaylistDetailScreen` — lazy track list, drag-to-reorder with `ReorderableLazyColumn`
7. Create `AddTrackScreen` — manual entry form (title, artist, duration)
8. Add empty state illustrations for Home and Detail screens
9. Implement swipe-to-delete on tracks with undo Snackbar

**Update `AI_DEV_LOG.md` after each of the 9 tasks above.**

---

## Phase 2 — Smart Filtering & Preferences

**Goal:** Add mood-aware filtering, sorting, and DataStore-backed user preferences.

### Tasks:
1. Implement filter chips on `HomeScreen` (All, Chill, Hype, Focus, Sad, Party)
2. Add sort options on `PlaylistDetailScreen` (by position, BPM, duration, date added)
3. Implement DataStore Proto for: default sort, theme preference, onboarding seen flag
4. Build `SettingsScreen` with toggle for dark/light, default mood filter, sort preference
5. Animate filter chip transitions with `AnimatedVisibility` + spring spec

**Update `AI_DEV_LOG.md` after each task.**

---

## Phase 3 — Spotify Integration

**Goal:** OAuth login with Spotify, import playlists, export changes back.

### Tasks:
1. Register app on Spotify Developer Dashboard (note: agent should scaffold the Retrofit service, actual client ID injected via `local.properties`)
2. Implement Spotify PKCE OAuth flow using Chrome Custom Tabs
3. Build `SpotifyApiService` with Retrofit:
   - `GET /me/playlists` — fetch user playlists
   - `GET /playlists/{id}/tracks` — fetch tracks
   - `POST /playlists` — create new playlist
   - `POST /playlists/{id}/tracks` — add tracks
4. Build `SpotifyImportScreen` — list Spotify playlists with checkbox selection → import to Room
5. Add "Push to Spotify" action in `PlaylistDetailScreen` overflow menu
6. Handle token refresh silently with OkHttp Authenticator interceptor

**Inject secrets via `local.properties` — never hardcode client ID or secret.**  
**Update `AI_DEV_LOG.md` after each task.**

---

## Phase 4 — Collaborative Playlists (Firebase)

**Goal:** Share playlists with other users via Firebase Firestore.

### Tasks:
1. Set up Firebase Auth (Google Sign-In + Anonymous fallback)
2. Mirror local `PlaylistEntity` to Firestore on "Make Collaborative" toggle
3. Generate shareable deep link (`musicshelf://playlist/{id}`)
4. Implement real-time listener on shared playlist — collaborators see track additions live
5. Build `CollabShareScreen` — share link, collaborator avatars (Coil), live track feed

**Update `AI_DEV_LOG.md` after each task.**

---

## Phase 5 — AI Playlist Generation

**Goal:** Accept a natural language vibe prompt and generate a tracklist via Gemini API.

### Tasks:
1. Build `AiSuggestScreen` — text field for vibe ("lo-fi study beats for 2am", "hype gym playlist"), submit button
2. Construct Gemini prompt: ask for 10–15 tracks as structured JSON (title, artist, mood, reason)
3. Parse response into `TrackEntity` list and preview screen
4. Allow user to select/deselect tracks, then save as new playlist
5. Show AI reasoning per track as a subtle subtitle ("because you tagged 'focus' playlists frequently")

**Update `AI_DEV_LOG.md` after each task.**

---

## Production Checklist

Before final build, the agent must verify:

- [ ] R8/ProGuard rules configured for Retrofit, Gson, Room, Hilt
- [ ] Firebase Crashlytics integrated
- [ ] All `collectAsState()` replaced with `collectAsStateWithLifecycle()`
- [ ] Edge-to-edge display enabled with `WindowCompat.setDecorFitsSystemWindows`
- [ ] Predictive back gesture support (`BackHandler` in all screens)
- [ ] No hardcoded strings — all in `strings.xml`
- [ ] No API keys in source — all in `local.properties` (gitignored)
- [ ] `AI_DEV_LOG.md` complete with all phases documented

---

## Agent Instructions

1. **Start with Phase 1, Task 1.** Do not skip ahead.
2. **After every task**, update `AI_DEV_LOG.md` with the entry format defined above.
3. **Ask before assuming** any Spotify client ID, Firebase config, or Gemini API key — scaffold the code but leave injection points clearly marked with `// TODO: inject from local.properties`.
4. **Prefer Compose over XML** for all UI. No legacy View system.
5. **No business logic in Composables.** All logic lives in ViewModel or UseCase.
6. **Error states are mandatory** — every screen must handle loading, error, and empty states.
7. **Accessibility:** All interactive elements must have `contentDescription`.

---

*MusicShelf Agent Spec — v1.0 | Built with Kotlin, Jetpack Compose, Firebase, Spotify API, Gemini API*
