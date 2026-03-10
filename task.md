# MusicShelf Project Status

## ✅ Phase 1: Local Core
- [x] Task 1: Project Setup (Hilt, Room, Nav, Theme)
- [x] Task 2: Room Schema (Entities, DAOs)
- [x] Task 3: Repositories & DI
- [x] Task 4: HomeScreen (Grid, Cards, ViewModels)
- [x] Task 5: CreatePlaylistScreen
- [x] Task 6: PlaylistDetailScreen
- [x] Task 7: AddTrackScreen
- [x] Task 8: Empty State Illustrations (Canvas)
- [x] Task 9: Swipe-to-Delete with Undo

## ✅ Phase 2: Smart Filtering & Preferences
- [x] Task 1: Filter Chips on HomeScreen
- [x] Task 2: Sort options on DetailScreen
- [x] Task 3: DataStore Proto for Sort/Theme/Onboarding
- [x] Task 4: SettingsScreen UI
- [x] Task 5: Animated Filter Chip transitions

## 🔄 Phase 3: Spotify Integration
- [x] Task 1: Scaffold SpotifyApiService & Repository
- [x] Task 2: Spotify PKCE OAuth flow (Custom Tabs)
- [x] Task 3: Token Exchange logic & Interceptors
- [x] Task 4: Main Activity Deep Link handling
- [ ] Task 5: SpotifyImportScreen (Checkbox selection → Room)
- [ ] Task 6: "Push to Spotify" action in overflow menu

## 🔄 Phase 4: Collaborative Playlists (Firebase)
- [x] Task 1: Firebase Auth (Google Sign-In + Anonymous)
- [/] Task 2: Mirror local PlaylistEntity to Firestore on toggle
- [ ] Task 3: Generate shareable deep link (musicshelf://playlist/{id})
- [ ] Task 4: Implement real-time listener on shared playlist
- [ ] Task 5: CollabShareScreen

## ❌ Phase 5: AI Playlist Generation
- [ ] Task 1: AiSuggestScreen (Vibe prompt UI)
- [ ] Task 2: Gemini API prompt construction (JSON)
- [ ] Task 3: Parse response and preview list
- [ ] Task 4: Save preview as new playlist
- [ ] Task 5: AI reasoning subtitles

## 🛠️ Production Checklist
- [ ] Edge-to-edge refinement
- [ ] Replace collectAsState() with collectAsStateWithLifecycle() (In Progress)
- [ ] String externalization final check
- [ ] API keys check (local.properties)
