# EZ Notes 📝

**EZ Notes** is a high-performance, production-ready, zero-network Android application engineered with **Jetpack Compose (Material 3)**, **Kotlin Coroutines & Flows**, **Room SQLite Database**, and a strict **MVVM + Repository** architecture.

Designed with an **offline-first philosophy**, EZ Notes guarantees 100% privacy with **zero network dependencies** and zero external data tracking.

---

## 🌟 Key Features

- **Notes & Ideas**:
  - Distraction-free text editor with clean typography.
  - Live word and character counting.
  - Pin important notes to the top of your notebook.
  - 8 Material 3 pastel color palettes for visual tagging.
  - Duplicate notes with a single tap.
  - Safe share sheet integration (export notes via local Intent without network).

- **Interactive Checklists & To-Dos**:
  - Fast task entry (add items with keyboard Done/Enter action).
  - Strike-through animations on completed items.
  - Real-time progress bar showing completion ratio (e.g., `4 of 5 completed - 80%`).
  - Clear completed tasks with one click.

- **Fast Organization & Search**:
  - Real-time debounced search across note titles, body text, and checklist items.
  - Category filter chips: `All`, `Notes`, `To-Dos`, and `Pinned`.
  - Toggle between 2-column Staggered Grid and Single Column List views.

- **Recycle Bin & Safe Trash**:
  - Accidental deletions are protected with an **Undo** Snackbar.
  - Dedicated **Trash** screen to restore notes or permanently erase them.
  - "Empty Trash" bulk purge with confirmation safeguards.

- **100% Offline & Private**:
  - Strict absence of `android.permission.INTERNET`.
  - No ads, no telemetry, no cloud tracking.
  - All data resides strictly in local encrypted device storage.

---

## 🛠️ Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI Toolkit** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Clean Repository Pattern |
| **State Management** | Kotlin StateFlow & SharedFlow |
| **Database** | Room 2.6.1 SQLite with KSP Code Generator |
| **Asynchrony** | Kotlin Coroutines & Reactive Flow Pipelines |
| **Navigation** | AndroidX Navigation Compose |
| **Min SDK / Target SDK**| Min SDK 26 (Android 8.0) / Target SDK 35 (Android 15) |

---

## 📁 Project Structure

```
D:\Antigravity\EZ Notes
├── app
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           ├── java/com/fawadini/eznotes
│           │   ├── EzNotesApp.kt
│           │   ├── MainActivity.kt
│           │   ├── data
│           │   │   ├── local
│           │   │   │   ├── converter/Converters.kt
│           │   │   │   ├── dao/NoteDao.kt
│           │   │   │   ├── database/EzNotesDatabase.kt
│           │   │   │   └── entity/NoteEntity.kt
│           │   │   ├── model
│           │   │   │   ├── Note.kt
│           │   │   │   ├── NoteColor.kt
│           │   │   │   ├── NoteType.kt
│           │   │   │   └── TodoItem.kt
│           │   │   └── repository
│           │   │       ├── NoteRepository.kt
│           │   │       └── NoteRepositoryImpl.kt
│           │   ├── ui
│           │   │   ├── components
│           │   │   │   ├── ColorSelector.kt
│           │   │   │   ├── ConfirmDialog.kt
│           │   │   │   ├── EmptyStateView.kt
│           │   │   │   └── NoteCard.kt
│           │   │   ├── navigation
│           │   │   │   ├── EzNavGraph.kt
│           │   │   │   └── Screen.kt
│           │   │   ├── screens
│           │   │   │   ├── HomeScreen.kt
│           │   │   │   ├── NoteEditScreen.kt
│           │   │   │   ├── SettingsScreen.kt
│           │   │   │   ├── TodoEditScreen.kt
│           │   │   │   └── TrashScreen.kt
│           │   │   ├── theme
│           │   │   │   ├── Color.kt
│           │   │   │   ├── Shape.kt
│           │   │   │   ├── Theme.kt
│           │   │   │   └── Type.kt
│           │   │   └── viewmodel
│           │   │       ├── HomeViewModel.kt
│           │   │       ├── NoteEditViewModel.kt
│           │   │       ├── SettingsViewModel.kt
│           │   │       ├── TodoEditViewModel.kt
│           │   │       ├── TrashViewModel.kt
│           │   │       └── ViewModelFactory.kt
│           │   └── util
│           │       ├── DateFormatter.kt
│           │       └── ShareHelper.kt
│           └── res
│               ├── drawable/
│               ├── mipmap-anydpi-v26/
│               ├── mipmap-hdpi/
│               ├── mipmap-mdpi/
│               ├── mipmap-xhdpi/
│               ├── mipmap-xxhdpi/
│               ├── mipmap-xxxhdpi/
│               ├── values/
│               └── xml/
├── gradle
│   ├── libs.versions.toml
│   └── wrapper
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

---

## 🎨 Store Listing Assets

Generated strictly at `D:\Antigravity\store listing\EZ Notes`:
- `icon.png`: **512x512 px** (2D Flat Minimalist Store Icon)
- `feature_graphic.png`: **1024x500 px** (High-Resolution Google Play Feature Banner)
