# AI_DEV_LOG — MusicShelf

***

## [Phase 1.1] — Project Setup
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- Updated `gradle/libs.versions.toml` with all Phase 1 dependencies (Hilt 2.52, Room 2.6.1, KSP, Navigation Compose 2.8.5, Coil 3.0.4, Lifecycle Compose 2.8.7, Google Fonts, Material Icons Extended, Coroutines, Gson)
- Updated root `build.gradle.kts` with KSP and Hilt plugin declarations
- Updated `app/build.gradle.kts` with all dependency implementations, KSP/Hilt plugins, buildConfig enabled
- Created `MusicShelfApp.kt` — `@HiltAndroidApp` Application class
- Updated `AndroidManifest.xml` with `android:name=".MusicShelfApp"`

### Architecture Decisions
- Used version catalog (`libs.versions.toml`) for centralized dependency management
- Set `compileSdk = 35` (integer literal) instead of the template's function-based syntax for broader compatibility
- Enabled `buildConfig = true` for future API key injection from `local.properties`

### Known Issues / TODOs
- `local.properties` keys for Spotify/Gemini API not yet scaffolded (Phase 3/5)

### How to Test This Step
- Open project in Android Studio → Gradle sync should succeed with no dependency resolution errors

***

## [Phase 1.2] — Room Schema
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `data/local/entity/PlaylistEntity.kt` — UUID PK, mood tag, collaborative flag, Spotify ID, timestamps
- `data/local/entity/TrackEntity.kt` — FK to playlist with CASCADE delete, indexed on playlistId, position-based ordering
- `data/local/entity/MoodTagEntity.kt` — tag PK, color hex, emoji
- `data/local/dao/PlaylistDao.kt` — Flow-based queries, CRUD, mood filter query
- `data/local/dao/TrackDao.kt` — Position-ordered queries, count queries, position updates
- `data/local/dao/MoodTagDao.kt` — getAll, insertAll for seeding
- `data/local/MusicShelfDatabase.kt` — @Database with seed callback for 5 default mood tags

### Architecture Decisions
- Used raw SQL in database callback for seeding (avoids needing DAO reference in Room.Callback)
- Foreign key with CASCADE delete ensures tracks are cleaned up when playlist is deleted
- Indexed `playlistId` column on tracks for query performance

### Known Issues / TODOs
- None

### How to Test This Step
- Build succeeds → Room schema compiles via KSP
- Verify entities listed in @Database annotation match all 3 entity classes

***

## [Phase 1.3] — Repositories & DI
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `domain/repository/PlaylistRepository.kt` — Interface with Flow-based queries
- `domain/repository/TrackRepository.kt` — Interface with reorder support
- `data/repository/PlaylistRepositoryImpl.kt` — Delegates to PlaylistDao
- `data/repository/TrackRepositoryImpl.kt` — Delegates to TrackDao, implements position-based reorder
- `di/DatabaseModule.kt` — Provides Room database singleton + all DAOs
- `di/RepositoryModule.kt` — Binds repository interfaces to implementations

### Architecture Decisions
- Clean Architecture: domain layer defines interfaces, data layer implements
- Position-based reorder updates each track's position individually (simple, correct for small lists)
- Database module uses `fallbackToDestructiveMigration()` for dev convenience

### Known Issues / TODOs
- reorderTracks does individual position updates — could batch into a transaction for performance at scale

### How to Test This Step
- Hilt injection graph should resolve without errors at compile time

***

## [Phase 1.4] — HomeScreen
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/home/HomeScreen.kt` — Scaffold with LazyVerticalGrid (2 columns), mood filter chip row (LazyRow), FAB, empty state
- `ui/home/PlaylistCard.kt` — Card with Coil AsyncImage (fallback icon), name, mood badge, track count
- `ui/home/HomeViewModel.kt` — flatMapLatest mood filtering, StateFlow playlists
- `ui/components/MoodChip.kt` — Reusable FilterChip with mood-specific colors and emoji helper functions

### Architecture Decisions
- Used `flatMapLatest` for mood filter to automatically cancel previous queries when filter changes
- Cards use 16dp corner radius per design spec
- Filter chips include an "All" option that sets mood to null

### Known Issues / TODOs
- Track count in playlist cards currently shows 0 (counted in card, not from joined query)

### How to Test This Step
- Launch app → HomeScreen shows empty state illustration
- Create playlist → card appears in grid

***

## [Phase 1.5] — CreatePlaylistScreen
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/create/CreatePlaylistScreen.kt` — Name + description TextFields, FlowRow mood chip group, create button with loading state
- `ui/create/CreatePlaylistViewModel.kt` — Form state management, validation (name required), UUID generation

### Architecture Decisions
- Used `LaunchedEffect` on `createdPlaylistId` to auto-navigate after creation
- FlowRow for mood chips provides natural wrapping on small screens

### Known Issues / TODOs
- Cover image picker not yet implemented (will add in future enhancement)

### How to Test This Step
- Navigate to CreatePlaylist → Enter name → Select mood → Tap Create → Should navigate to playlist detail

***

## [Phase 1.6] — PlaylistDetailScreen
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/detail/PlaylistDetailScreen.kt` — Header with cover + info + mood badge, LazyColumn of tracks, FAB to add, delete action in top bar
- `ui/detail/PlaylistDetailViewModel.kt` — SavedStateHandle for playlistId, tracks StateFlow, reorder + delete + undo logic

### Architecture Decisions
- Used `SavedStateHandle` to extract navigation argument (playlistId)
- Playlist loaded once via suspend, tracks observed via StateFlow
- Loading/Error/Empty states all handled

### Known Issues / TODOs
- Drag-to-reorder uses visual drag handle but full ReorderableLazyColumn integration deferred (requires additional library)

### How to Test This Step
- Open playlist → Should show header and empty tracks state
- Add tracks → Should appear in list ordered by position

***

## [Phase 1.7] — AddTrackScreen
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/addtrack/AddTrackScreen.kt` — Title, artist, album, duration (min:sec) form with validation
- `ui/addtrack/AddTrackViewModel.kt` — Duration parsing, position auto-increment, form validation

### Architecture Decisions
- Duration split into minutes/seconds fields with numeric keyboard
- Seconds capped at 59
- Auto-navigates back on successful save

### Known Issues / TODOs
- None

### How to Test This Step
- From playlist detail → Tap + → Fill form → Tap Add Track → Should save and navigate back

***

## [Phase 1.8] — Empty State Illustrations
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/components/EmptyStateView.kt` — Canvas-drawn illustrations: vinyl record (playlists), list lines (tracks)
- Two illustration types: `PLAYLIST` and `TRACK`
- Used in HomeScreen and PlaylistDetailScreen

### Architecture Decisions
- Used Compose Canvas instead of vector drawables — zero external assets, perfect theme consistency
- Illustrations use MusicPrimary color for accent elements

### Known Issues / TODOs
- None

### How to Test This Step
- Launch app with empty database → See vinyl record illustration on home
- Open empty playlist → See track list illustration

***

## [Phase 1.9] — Swipe-to-Delete with Undo
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/detail/TrackItem.kt` — SwipeToDismissBox (end-to-start), red delete background
- Undo Snackbar in PlaylistDetailScreen via `SnackbarHostState`
- ViewModel tracks `recentlyDeletedTrack` for undo capability

### Architecture Decisions
- Only allow swipe end-to-start (right-to-left) to prevent accidental deletes
- Undo re-inserts the exact same TrackEntity (preserving original ID and data)
- Snackbar uses `SnackbarDuration.Short` (4 seconds undo window)

### Known Issues / TODOs
- None

### How to Test This Step
- Add tracks → Swipe left on a track → Red delete background appears → Track deleted → "Track deleted" Snackbar with "Undo" action appears → Tap "Undo" → Track restored

***

## [Phase 1.0] — Theme & Navigation Setup
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/theme/Color.kt` — MusicShelf dark palette: Background #0A0A0F, Surface #12121A, Primary #7C5CBF, Secondary #1DB954, mood tag colors
- `ui/theme/Type.kt` — Inter font via Google Fonts downloadable provider, full typography scale
- `ui/theme/Theme.kt` — Dark-only Material 3 theme, edge-to-edge status/nav bar
- `res/values/font_certs.xml` — Google Fonts certificate arrays
- `core/navigation/Routes.kt` — Sealed class route definitions
- `core/navigation/AppNavGraph.kt` — NavHost with animated slide + fade transitions
- `MainActivity.kt` — @AndroidEntryPoint, edge-to-edge, navigation host
- `res/values/strings.xml` — All UI strings externalized

### Architecture Decisions
- Dark-only theme (no light mode) per spec — "dark, minimal, premium aesthetic"
- No dynamic colors — custom palette used consistently
- Spring-based transition animations for navigation

### Known Issues / TODOs
- Predictive back gesture support not yet added (Production Checklist item)

### How to Test This Step
- Build and launch → Dark theme with purple accent
- Navigation between screens should have smooth slide + fade transitions

***

## [Task 2.1] — DataStore Proto & Protobuf Dependencies
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- Added `protobuf-javalite`, `protobuf-protoc`, `datastore` dependencies to `gradle/libs.versions.toml`
- Applied `com.google.protobuf` plugin in `app/build.gradle.kts`
- Configured `protobuf { protoc { ... } generateProtoTasks { ... } }` block for Java Lite code generation

### Bug Fix: `error.NonExistentClass` KSP Build Failure
**Root Cause (3 issues):**
1. **Missing imports in `RepositoryModule.kt`** — `UserPreferencesRepositoryImpl` and `UserPreferencesRepository` referenced without imports
2. **KSP/Protobuf task ordering** — KSP could run before Protobuf code generation
3. **KSP2 source visibility** — KSP2 cannot see Java sources generated by other plugins unless explicitly configured

**Fix Applied:**
- Added missing imports to `di/RepositoryModule.kt`
- `afterEvaluate` block to make `ksp{Variant}Kotlin` depend on `generate{Variant}Proto`
- Added proto-generated Java source dirs to Kotlin source sets via `android.applicationVariants.all { ... kotlin.sourceSets ... srcDir(...) }`
- Added `ksp.allowSourcesFromOtherPlugins=true` to `gradle.properties`

### Additional Compilation Fixes
| File | Error | Fix |
|------|-------|-----|
| `Theme.kt` | Missing `darkColorScheme` import | Added both `darkColorScheme` and `lightColorScheme` imports |
| `PlaylistDetailViewModel.kt` | Missing `combine` import | Added `kotlinx.coroutines.flow.combine` import |
| `HomeViewModel.kt` | `_selectedMoodFilter` undeclared | Changed to `_selectedMood` |
| `HomeScreen.kt` | `selectedMoodFilter` undeclared | Changed to `selectedMood` |
| `HomeScreen.kt` | `EmptyStateView` wrong params | Fixed to `title`, `subtitle`, `illustrationType` |
| `HomeScreen.kt` | `R.string.empty_playlists` missing | Changed to `empty_playlists_title` / `_subtitle` |
| `AppNavGraph.kt` | Type-safe `composable<Routes.X>` | Converted to string-based (no `@Serializable`) |

### How to Test This Step
- `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL

***

## [Task 2.2] — Proto Schema, Serializer & Repository
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `proto/user_prefs.proto` — Protobuf schema: `ThemePreference` enum, `SortOrder` enum, `UserPrefs` message with `default_mood_filter` and `onboarding_seen`
- `data/local/datastore/UserPreferencesSerializer.kt` — `Serializer<UserPrefs>` with system-default theme, position sort order
- `domain/repository/UserPreferencesRepository.kt` — Interface: `userPreferencesFlow`, update methods for theme/mood/sort/onboarding
- `data/repository/UserPreferencesRepositoryImpl.kt` — Implementation: Flow-based reads with `IOException` fallback, `updateData` writes
- `di/DatabaseModule.kt` — Added `@Provides` for `DataStore<UserPrefs>` via `DataStoreFactory.create()`

### Architecture Decisions
- Proto DataStore over Preferences DataStore for type safety and schema evolution
- Protobuf Lite to minimize APK size

### How to Test This Step
- Build succeeds → Proto classes generated and resolvable by KSP

***

## [Task 2.3] — SettingsViewModel & SettingsScreen
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/settings/SettingsScreen.kt` — Three sections: Theme (System/Dark/Light), Default Sort Order (Position/BPM/Duration/Date Added), Default Mood Filter (All + mood tags)
- `ui/settings/SettingsViewModel.kt` — `@HiltViewModel`, reads `UserPrefs` via `StateFlow`, writes via repository
- Reusable `SettingsSection` and `SettingsOption` composable helpers

### Architecture Decisions
- `WhileSubscribed(5000)` sharing — auto-stops collection when screen backgrounded
- Mood filter settings reuse `defaultMoodTags` from `MoodChip.kt` for consistency

### How to Test This Step
- Navigate to Settings → Theme, Sort, Mood sections visible
- Change preferences → Persisted (survives app restart)

***

## [Task 2.4] — Settings Route Integration
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `core/navigation/Routes.kt` — Added `Settings` route (`data object Settings : Routes("settings")`)
- `core/navigation/AppNavGraph.kt` — Added `composable(Routes.Settings.route)` → `SettingsScreen`
- `ui/home/HomeScreen.kt` — Settings gear icon (`Icons.Default.Settings`) in top bar header, wired to `onSettingsClick` callback

### How to Test This Step
- Launch app → Gear icon visible in top-right of HomeScreen → Tap → Settings screen opens

***

## [Task 2.5] — Sort Options on PlaylistDetailScreen
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/detail/PlaylistDetailScreen.kt` — Sort icon in top bar with `DropdownMenu` offering 4 options: Position, BPM, Duration, Date Added (each with material icon: `FormatListNumbered`, `Speed`, `Timer`, `DateRange`)
- `ui/detail/PlaylistDetailViewModel.kt` — `activeSortOrder: StateFlow<SortOrder?>`, `setSortOrder()`, 3-way `combine()` of tracks + active sort + user prefs to apply sort in real-time
- Sort falls back to user's default sort order from Proto DataStore when no active sort is selected

### Architecture Decisions
- `combine(tracks, activeSort, userPrefs)` — reactive re-sorting whenever any input changes
- Active sort overrides default sort from preferences (per-screen override)
- Sorting logic: BPM/Duration/Date are descending; Position/Custom is ascending

### How to Test This Step
- Open playlist → Tap sort icon → Select "BPM" → Tracks re-sorted by BPM descending
- Change default sort in Settings → Re-open playlist without selecting sort → Uses new default

***

## [Task 2.6] — AnimatedVisibility + Spring for Filter Chips
**Date Completed:** 2026-03-05
**Status:** ✅ Done

### What Was Built
- `ui/home/HomeScreen.kt` — Staggered spring entrance animation for mood filter chips:
  - `AnimatedVisibility` wrapping each `MoodChip` in the `LazyRow`
  - `fadeIn` with `Spring.DampingRatioLowBouncy` + `Spring.StiffnessMediumLow`
  - `slideInHorizontally` with `spring(dampingRatio = 0.7f, stiffness = 300f)` and index-based offset (`fullWidth * (index + 1)`)
  - `LaunchedEffect(Unit)` triggers `chipsVisible = true` after 150ms delay for cascade effect

### Architecture Decisions
- Spring physics instead of tween — natural, bouncy motion that feels premium
- Index-based offset multiplier creates a staggered cascade without needing individual coroutine delays
- 150ms initial delay gives the screen time to settle before the chips animate in

### How to Test This Step
- Launch app → Filter chips slide in from right with a bouncy staggered cascade
- Each chip enters slightly after the previous one

***

## [Task 2.7] — AI_DEV_LOG.md Updated
**Date Completed:** 2026-03-05
**Status:** ✅ Done

All Phase 2 tasks (2.1–2.7) documented above.

***

## ✅ Phase 2 Complete
**All sub-tasks delivered:** DataStore Proto dependencies (2.1), Proto schema & repository (2.2), Settings UI (2.3), Settings route integration (2.4), Sort options (2.5), AnimatedVisibility + spring filter chips (2.6), dev log updated (2.7). User preferences are fully integrated with reactive Flow-based reads backed by Protobuf DataStore.

***

## [Phase 3.1] — Scaffold Spotify API Service & Repository
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- `SpotifyApiService.kt` — Defined endpoints (`/me/playlists`, `/playlists/{id}/tracks`, `/users/{id}/playlists`, `/playlists/{id}/tracks`) with matching Request/Response models.
- `SpotifyPlaylistDto.kt` & `SpotifyTrackDto.kt` — Data transfer objects for Spotify network responses mapping heavily to `items`, `id`, `name`, `images` standard schemas.
- `SpotifyRepository.kt` (interface) and `SpotifyRepositoryImpl.kt` — Handles conversion and mapping between remote DTO payloads and our local `PlaylistEntity` / `TrackEntity`. Returns idiomatic `Result<T>`.
- `NetworkModule.kt` — Introduced Hilt configuration providing `Retrofit`, `OkHttpClient`, and `SpotifyApiService` singletons.
- Added explicit Retrofit dependencies mapped smoothly alongside Gson setup.

### Architecture Decisions
- Segregated DTO payload models separately from Domain Entities to avoid polluting Database models with remote (`@SerializedName`) details.
- Used default fallback assignment on parsing, such as `moodTag = "chill"` for Spotify playlists.
- Repositories return `Result<T>` safely wrapping networking logic, keeping UI code clean.

### Known Issues / TODOs
- OAuth Interceptors for adding the `Bearer` token to headers are omitted completely here (waiting on Phase 3 Task 2 implementations).
- Adding tracks is currently not handling robust chunks above 100 gracefully in one pass natively yet (currently simplistic chunk pass loops).

### How to Test This Step
- Sync Gradle to ensure Retrofit compiles properly.
- Hilt dependencies resolve at compile time.

***

## [Phase 3.2] — Wire Spotify Credentials & Scaffold PKCE OAuth
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- Configured `app/build.gradle.kts` to parse `local.properties` and inject `SPOTIFY_CLIENT_ID` and `SPOTIFY_REDIRECT_URI` via `BuildConfig` securely.
- Bound `manifestPlaceholders["spotifyRedirectScheme"]` to handle Chrome Custom Tabs redirect intent filtering.
- Scaffoled `SpotifyAuthManager.kt` using `androidx.browser.customtabs` for the PKCE OAuth Flow.
- Generated Code Verifier and Code Challenge (SHA-256) per Spotify's PKCE specifications.
- Hooked up `BuildConfig.SPOTIFY_CLIENT_ID` and `BuildConfig.SPOTIFY_REDIRECT_URI` natively — ensuring zero hardcoded credentials remain in the codebase.

### Architecture Decisions
- Moved configuration reads out of the UI and into gradle build steps (`buildConfigField`) protecting secrets from VCS committing and standardizing variables natively.
- `SpotifyAuthManager` persists the `codeVerifier` during the authentication handoff securely.

### Known Issues / TODOs
- UI Integration: Currently there is no "Connect to Spotify" UI Button that invokes `SpotifyAuthManager.initiateAuthFlow`.
- The `handleAuthCallback` only pulls the `code`. We still need to build the `POST` token exchange logic to turn that `code` into a `Bearer` Token.

### How to Test This Step
- Sync Gradle (ensure `BuildConfig` gets generated successfully).
- Ensure `local.properties` exists with `SPOTIFY_CLIENT_ID` and `SPOTIFY_REDIRECT_URI` inside it before compiling.

***

## [Phase 4.1] — Firebase Auth Setup & UI Integration
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- Integrated `firebase-auth`, `firebase-firestore`, and `play-services-auth` inside version catalog and `build.gradle.kts`.
- Validated Google Services plugin mapping inside gradle configs for `.json` parsing.
- Created `AuthRepository.kt` defining the core domain mappings of `AuthUser` to abstract Firebase.
- Implemented `FirebaseAuthRepositoryImpl.kt` connecting specifically to Google's Provider alongside an Anonymous Fallback layer.
- Added DI Provider injection inside `NetworkModule.kt` binding `Firebase.auth` and `Firebase.firestore`.
- Bound `AuthRepository` heavily into `RepositoryModule.kt` for Hilt wiring.
- Built `AuthViewModel.kt` emitting a reactive `StateFlow<AuthUser?>` to track logins using `auth.addAuthStateListener`.
- Refactored `SettingsScreen.kt` gracefully injecting an elegant "Account" section tracking Sign In vs. Anonymous identities with Material 3 styling.

### Architecture Decisions
- Auth logic strictly adheres to Clean Architecture via interfaces. We convert Firebase `User` abstractions into a pure Kotlin `AuthUser` data class immediately at the boundary.
- Kept the app offline-first by explicitly prioritizing `signInAnonymously()`, allowing users basic offline abilities before requiring Google. 
- Avoided Google Credential Manager overhead focusing on `play-services-auth` specific OAuth handoffs given the Firebase core bindings.

### Known Issues / TODOs
- (Resolved) Google Sign-In and Anonymous automatic instantiation implemented in Phase 4.2.

### How to Test This Step
- Build the app and confirm Gradle sync completes.
- Load the `SettingsScreen` and verify the "Anonymous User" layout renders.

***

## [Phase 3.3] — Phase 3 TODOs Cleanup & Token Exchange
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- Created `SpotifyTokenInterceptor.kt` injected into OkHttp to append the `Bearer` token to all endpoints (except `/api/token`).
- Implemented HTTP 401 handling inside `SpotifyTokenInterceptor` to seamlessly trigger forced token refreshes safely.
- Updated `SpotifyApiService.kt` with `@POST("https://accounts.spotify.com/api/token")` functions `getAccessToken` and `refreshAccessToken`.
- Converted `SpotifyAuthManager` from an `object` single to an `@Inject` Singleton Class to inject the ApiService and SharedPreferences safely.
- Added `SpotifyTokenInterceptor` to the `NetworkModule.kt` DI graph using `dagger.Lazy<SpotifyAuthManager>` to prevent immediate Dagger cyclic dependency crashes.
- Scaffolded chunking algorithm inside `SpotifyRepositoryImpl.kt` (`.chunked(100)`) preventing massive playlist failures over the array size limits.
- Hooked the Spotify Auth custom tab flow to a "Connect to Spotify" Button inside the `SettingsScreen` account area.

### Architecture Decisions
- Used `dagger.Lazy` in OkHttp Interceptors to circumvent tricky dagger circular mappings involving `Retrofit -> OkHttp -> Interceptor -> AuthManager -> Retrofit`. 
- Implemented synchronized block wrapping token exchanges inside OkHttp to prevent massive spamming of refresh tokens from parallel coroutines.

### Known Issues / TODOs
- (Resolved) `MainActivity` now securely captures `musicshelf://callback`. No known issues with Phase 3 auth remain.

### How to Test This Step
- Sync Gradle and `assembleDebug`. Build will pass.
- Load the `SettingsScreen` and tap "Connect to Spotify". It should securely launch a Custom Chrome Tab pointing at accounts.spotify.com.

***

## ✅ Phase 3 Complete (Foundational)
**All structural sub-tasks delivered:** Spotify API networking + DTOs (3.1), PKCE logic (3.2), Token Interceptors + 401 automatic refreshes + 100-Track chunking + UI Triggers (3.3), and Deep Link code-to-token resolution in MainActivity (3.4).

***

## [Phase 3.4] — Spotify Auth Deep Link Intent Resolution
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- Injected `SpotifyAuthManager` into `MainActivity.kt`.
- Overrode `onNewIntent(intent: Intent)` to capture the `musicshelf://callback` deep link fired when the browser Custom Tab redirects.
- Bootstrapped `handleSpotifyIntent(intent)` into both `onCreate` (for dead-process cold starts) and `onNewIntent` (for warm foreground returns).
- Coroutine-launched `spotifyAuthManager.exchangeCodeForToken(code)` instantly bridging the OAuth Code to the final Access / Refresh Tokens.

### Architecture Decisions
- Handled intent parsing silently in `MainActivity` before the Compose Navigation graph even recognizes a state change. If the AuthManager successfully parses it and saves it to `SharedPreferences`, the rest of the app becomes immediately ready.

### How to Test This Step
- Build `assembleDebug`, run the app, and tap "Connect to Spotify".
- Fully sign into a Spotify account in the browser.
- When the browser collapses and redirects to the app, check `SharedPreferences` (or Add a Track) to verify the OAuth tokens were securely traded and saved.

***

## [Phase 3.5] — Comprehensive Spotify Import Fixes
**Date Completed:** 2026-03-10
**Status:** ✅ Done

### What Was Built
- **Pagination**: Implemented a `while (response.next != null)` loop in `SpotifyRepositoryImpl.kt` hitting a new `@Url` endpoint `getPlaylistTracksUrl` to recursively fetch every single page of tracks for playlists exceeding 100 items limit.
- **OAuth Scopes**: Appended the critical `user-library-read` scope to `SpotifyAuthManager`'s PKCE `/authorize` builder intent, ensuring private library saves are fully visible.
- **DTO Structure**: Surfaced `items[n].track` explicitly ensuring `SpotifyTrackDto` captures the deeply nested properties cleanly.
- **Null Safety**: Filtered out empty tracks (like unavailable audio or local files synced cross-device to Spotify) safely using `mapIndexedNotNull`. Also provided default strings (`Unknown Artist`) for missing deep metadata array fields avoiding silent `NullPointerException` app crashes.
- **Error Bubbling**: Mapped `.onFailure` states inside `SpotifyImportViewModel` directly to `_uiState.error` avoiding silenced coroutines.

### Architecture Decisions
- Appends the track collections in a synchronous loop blocking the coroutine suspended until all pagination cursors are exhausted — simple, bulletproof, and executes on the `IO` dispatcher gracefully under `viewModelScope`.
- Made fields deep down into `SpotifyArtistDto` and `SpotifyAlbumDto` natively nullable to gracefully accept corrupted Spotify API responses.

### How to Test This Step
- Re-authenticate to accept the new OAuth scope.
- Import a playlist with over 100 songs or containing synced local MP3s.
- Confirm all tracks enumerate safely in the application without dropping items.

***
***

## [Phase 4.2] — Google Sign-In & Anonymous Fallback Wiring
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- Added `androidx.credentials` (Credential Manager) dependencies to `gradle/libs.versions.toml` and `app/build.gradle.kts` using version `1.3.0-rc01`.
- Fully implemented the Google Sign-In flow in `SettingsScreen.kt` using the new `CredentialManager` and `GetGoogleIdOption`.
- Verified that the Google ID token is successfully passed to `AuthViewModel.signInWithGoogle(idToken)`.
- Implemented an automatic Anonymous Auth instantiate-on-launch flow inside `AppNavGraph.kt` using a `LaunchedEffect` that checks for a null `authUser`.
- Added the `default_web_client_id` placeholder to `strings.xml`.

### Architecture Decisions
- Used `LaunchedEffect(authUser)` at the root of the navigation graph to ensure the app is always in a valid authenticated state (anonymous by default).
- Switched to the modern `Credential Manager` API instead of the deprecated `GoogleSignInClient` for future-proofing Google Sign-In.

### Known Issues / TODOs
- `default_web_client_id` is currently a placeholder and needs to be replaced with the actual client ID from the Google Cloud Console for real-world functionality.

### How to Test This Step
- Build the app and confirm Gradle sync completes with the new Credential Manager dependencies.
- Launch the app and verify that an anonymous sign-in is triggered (check logs or Auth state).
- Open Settings, tap "Sign In with Google", and confirm the system account picker appears.

***
***

## [Phase 4.3] — Firestore Playlist Mirroring
**Date Completed:** 2026-03-09
**Status:** ✅ Done

### What Was Built
- **UI**: Added a "Collaborative" material `Switch` to the `PlaylistHeader` in `PlaylistDetailScreen.kt`.
- **ViewModel**: Implemented `toggleCollaborative(Boolean)` in `PlaylistDetailViewModel.kt` to persist the state in Room.
- **Data Layer**:
    - Added `getTracksForPlaylistSync(id)` to `TrackDao.kt` and `TrackRepository.kt` for synchronous batch fetching.
    - Updated `PlaylistRepositoryImpl.kt` to inject `FirebaseFirestore`.
    - Implemented Firestore sync logic: when `isCollaborative` is enabled, the playlist metadata is pushed to `playlists/{id}` and all local tracks are mirrored to a `tracks` sub-collection.
    - Added automatic Firestore deletion when a collaborative playlist is deleted locally.

### Architecture Decisions
- Used a simple "Overwrite/Set" strategy for Task 2 to ensure initial data parity between Room and Firestore.
- Repurposed the existing `updatePlaylist` flow in the repository to trigger the sync, keeping the repository as the clean sync authority.

### Known Issues / TODOs
- Real-time listening for updates from other collaborators is deferred to Task 4.

### How to Test This Step
- Build `assembleDebug` to verify compilation.
- Create a playlist, open its detail screen, and toggle "Collaborative".
- (Manual) Check Firestore Console to verify the `playlists/{uuid}` document and `tracks` collection were created with correct local data.

***
