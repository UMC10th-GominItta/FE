# Gominitta Android

Team-shared Android repository foundation — Kotlin + Jetpack Compose.

## Stack at a Glance

| Layer | Choice | Why locked |
|---|---|---|
| Language | Kotlin 2.2.10 | Official first-class Android language |
| UI | Jetpack Compose (Material 3) | No XML/View system |
| DI | Hilt 2.59.2 | Official Jetpack DI, built on Dagger |
| Navigation | Navigation-Compose 2.9.0 | Compose-native; hidden behind Routes constants |
| Build | AGP 9.2.1 + Gradle 9.4.1 | Stable, KSP-compatible |
| Annotations | KSP 2.2.10-2.0.2 | Replaces KAPT; faster incremental builds |
| SDK | compileSdk/targetSdk 37, minSdk 26 | Android 17 target; covers Android 8.0+ devices |

---

## Folder Structure

```
app/src/main/java/com/gominitta/android/
├── GominittaApplication.kt    # @HiltAndroidApp — Hilt entry point
├── MainActivity.kt             # @AndroidEntryPoint — single-activity shell; hosts GominittaTheme + AppNavHost
├── di/                         # Hilt modules (AppModule.kt binds interfaces → implementations)
├── data/
│   └── repository/             # Repository interfaces + Fake/Mock implementations
├── navigation/
│   ├── Routes.kt               # ALL route strings live here (thin route API)
│   └── AppNavHost.kt           # NavHost — only file that owns NavController
├── ui/
│   ├── theme/
│   │   ├── Color.kt            # Design token: color roles (placeholder values)
│   │   ├── Type.kt             # Design token: typography scale
│   │   ├── Spacing.kt          # Design token: 4dp-grid spacing scale
│   │   ├── Shape.kt            # Design token: corner-radius scale
│   │   └── Theme.kt            # GominittaTheme — wires all tokens into MaterialTheme
│   └── components/             # Shared Composable components (Gominitta* wrappers)
└── feature/
    ├── home/                   # One package per screen
    │   ├── HomeScreen.kt
    │   └── HomeViewModel.kt
    └── detail/
        └── DetailScreen.kt
```

---

## Architecture Overview

How the layers wire together at runtime:

```
GominittaApplication (@HiltAndroidApp)
  └── MainActivity (@AndroidEntryPoint)
        └── GominittaTheme            ← design tokens active for every child composable
              └── AppNavHost           ← owns NavController; maps Routes.* → Screens
                    ├── HomeScreen     ← receives onNavigateToDetail: () -> Unit lambda
                    │     └── HomeViewModel (@HiltViewModel)
                    │           └── SampleRepository (interface)
                    │                 └── FakeSampleRepository  ← @Binds in AppModule
                    └── DetailScreen   ← receives onNavigateBack: () -> Unit lambda
```

Key invariants to preserve:
- **Only `AppNavHost.kt` holds a `NavController` reference** — screens never import it.
- **ViewModels inject interfaces**, never concrete classes.
- **All composables access visual properties via `MaterialTheme.*`** — never hardcoded values.

---

## Naming Conventions

### Screens / Features
- Each screen lives in `feature/<screenName>/`
- Screen composable: `<ScreenName>Screen.kt` (e.g. `HomeScreen.kt`)
- ViewModel (if needed): `<ScreenName>ViewModel.kt`
- Route constant in `Routes.kt`: `const val HOME = "home"` (lowercase, no spaces)

### Navigation routes
- Simple: `"home"`, `"settings"`, `"profile"`
- With argument: define two entries in `Routes.kt` — a template for registration
  and a helper function for navigation calls:
  ```kotlin
  // Routes.kt
  const val ITEM_DETAIL = "item_detail/{itemId}"          // used in composable()
  fun itemDetailRoute(id: String) = "item_detail/$id"     // used in navigate()
  ```
  Then in `AppNavHost.kt`:
  ```kotlin
  composable(
      route = Routes.ITEM_DETAIL,
      arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
  ) { backStackEntry ->
      val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
      ItemDetailScreen(itemId = itemId, onNavigateBack = { navController.popBackStack() })
  }
  ```
- **Never hardcode route strings** outside `Routes.kt`.

### Repository layer
- Interface: `XxxRepository` in `data/repository/`
- Fake impl: `FakeXxxRepository` (default for DI)
- Real impl (future): `RemoteXxxRepository` or `LocalXxxRepository`

---

## Design System

The design system uses **placeholder token values**. Real values are extracted
from Figma in a dedicated design-system sprint.

### Token Files

| File | Token category | How to access in Compose |
|---|---|---|
| `ui/theme/Color.kt` | Colour tokens (`PrimaryDefault`, `SurfaceVariant` …) | `MaterialTheme.colorScheme.*` |
| `ui/theme/Type.kt` | Typography scale (Material 3: Display/Headline/Body/Label) | `MaterialTheme.typography.*` |
| `ui/theme/Spacing.kt` | Spacing scale (4dp grid: `xxs` → `xxxl`) | `MaterialTheme.spacing.*` |
| `ui/theme/Shape.kt` | Shape scale (ExtraSmall → ExtraLarge corners) | `MaterialTheme.shapes.*` |
| `ui/theme/Theme.kt` | `GominittaTheme {}` — wire all tokens into MaterialTheme | Wrap every screen root |

Quick token examples inside composables:
```kotlin
// Spacing
MaterialTheme.spacing.md          // 16.dp
MaterialTheme.spacing.lg          // 24.dp

// Color
MaterialTheme.colorScheme.primary
MaterialTheme.colorScheme.onSurfaceVariant

// Typography
MaterialTheme.typography.headlineMedium
MaterialTheme.typography.bodySmall

// Shape
MaterialTheme.shapes.medium       // 12.dp corners (default card)
MaterialTheme.shapes.extraSmall   // 4.dp corners (chips, text fields)
```

Replace only the **values** in `Color.kt` / `Type.kt` / `Spacing.kt` / `Shape.kt`
when the Figma hand-off is ready. Token **names** must stay stable so feature
code is not affected.

### Component Hierarchy

Shared UI components live in `ui/components/` and wrap Material 3 primitives
so that global design changes (token updates) propagate without touching feature code.

```
ui/components/
├── GominittaButton.kt   ← wraps Material3 Button  (labelLarge type, primary colour, small shape)
└── GominittaCard.kt     ← wraps Material3 ElevatedCard (medium shape, surface colour, md padding)
```

**Rules for adding new components:**
1. Name pattern: `Gominitta<Component>.kt` (e.g. `GominittaTextField.kt`)
2. Accept `modifier: Modifier = Modifier` as last-before-content param.
3. Drive all visual decisions through `MaterialTheme.*` tokens — no hardcoded colours or sizes.
4. Include `@Preview` variants for light + dark + state (disabled, error…).
5. Feature screens import from `ui/components/`; they never call Material 3 widgets directly.

---

## Dependency Management

All versions live in **`gradle/libs.versions.toml`** (the Gradle version
catalog). Rules:

1. Add new dependencies to `libs.versions.toml` first.
2. Reference them via `libs.*` aliases in `build.gradle.kts` — no string literals.
3. Never duplicate a version number. Use `version.ref` to point at the
   `[versions]` table.

---

## Getting Started

```bash
# Clone
git clone <repo-url>
cd gominitta-android

# Build (first run downloads dependencies — needs internet or local Maven cache)
./gradlew assembleDebug

# Run on connected device / emulator
./gradlew installDebug
```

---

## What's Out of Scope (Deferred Behind Seams)

| Item | Where the seam is |
|---|---|
| Networking (Retrofit / Ktor) | `SampleRepository` interface |
| Local database (Room / DataStore) | `SampleRepository` interface |
| Real Figma token values | `Color.kt`, `Type.kt`, `Spacing.kt` |
| Multi-module split | Single `:app` with feature packages |
| Reference feature screen | `feature/home/HomeScreen.kt` placeholder |

Swap any item by implementing behind the existing seam; no changes to callers.
