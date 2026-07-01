# Contributing to Gominitta Android

## Setup

1. Android Studio Ladybug or later (bundled JBR 17 required)
2. Android SDK with **API 37 platform** + build-tools 37.0.0 (compileSdk = 37, targetSdk = 37)
3. `./gradlew assembleDebug` must pass before any commit

## Code Style

- **Kotlin code style**: `official` (enforced via `gradle.properties`)
- **No XML layouts**: Jetpack Compose only
- **Formatting**: run `./gradlew ktlintFormat` (add ktlint when tooling sprint starts)

## Adding a New Screen

**Simple screen (no ViewModel):**

1. Create `feature/<screenName>/` package
2. Add `<ScreenName>Screen.kt` — accept navigation actions as plain lambdas only:
   ```kotlin
   @Composable
   fun SettingsScreen(
       onNavigateBack: () -> Unit,
       modifier: Modifier = Modifier,
   ) { … }
   ```
3. Add `const val SETTINGS = "settings"` to `navigation/Routes.kt`
4. Add the composable entry in `navigation/AppNavHost.kt`:
   ```kotlin
   composable(Routes.SETTINGS) {
       SettingsScreen(onNavigateBack = { navController.popBackStack() })
   }
   ```

**Screen with ViewModel:**

5. Add `<ScreenName>ViewModel.kt` in the same feature package:
   ```kotlin
   @HiltViewModel
   class SettingsViewModel @Inject constructor(
       private val myRepo: MyRepository,   // inject interface, never concrete
   ) : ViewModel() { … }
   ```
6. In the screen composable, inject with `hiltViewModel()`:
   ```kotlin
   fun SettingsScreen(…, viewModel: SettingsViewModel = hiltViewModel()) { … }
   ```

**Screen with route argument:**

7. Add both a template constant and a navigation helper to `Routes.kt`:
   ```kotlin
   const val ITEM_DETAIL = "item_detail/{itemId}"
   fun itemDetailRoute(id: String) = "item_detail/$id"
   ```
8. Register the route with argument parsing in `AppNavHost.kt`:
   ```kotlin
   composable(
       route = Routes.ITEM_DETAIL,
       arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
   ) { backStackEntry ->
       val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
       ItemDetailScreen(itemId = itemId, onNavigateBack = { navController.popBackStack() })
   }
   ```

## Adding a Dependency

1. Add version to `[versions]` in `gradle/libs.versions.toml`
2. Add library entry to `[libraries]` using `version.ref`
3. Reference via `libs.<alias>` in `app/build.gradle.kts`
4. Never hardcode version strings in `build.gradle.kts`

## Data Layer

- Define a new `XxxRepository` interface in `data/repository/`
- Implement `FakeXxxRepository` returning deterministic in-memory data
- Bind the fake in `di/AppModule.kt` with `@Binds`
- Feature code depends only on the interface, never the concrete class

## Design Tokens

Tokens live in `ui/theme/`. **Never use hardcoded color/spacing/shape values** in
feature composables. Use only:

| Token category | Access pattern |
|---|---|
| Colour | `MaterialTheme.colorScheme.*` |
| Typography | `MaterialTheme.typography.*` |
| Spacing | `MaterialTheme.spacing.*` (custom extension) |
| Shape | `MaterialTheme.shapes.*` |

When real Figma values arrive:
1. Update only the hex/sp/dp values in `Color.kt`, `Type.kt`, `Spacing.kt`, `Shape.kt`
2. Token **names** (API) must not change — feature code must compile unchanged

## Adding a Shared Component

1. Create `ui/components/Gominitta<Name>.kt`
2. Wrap the corresponding Material 3 primitive (Button → `GominittaButton`, Card → `GominittaCard`)
3. Accept `modifier: Modifier = Modifier` (last param before `content`)
4. Drive all styling via `MaterialTheme.*` tokens — no hardcoded values
5. Add `@Preview` for light + dark + any notable states
6. Feature screens must use `Gominitta*` components, not raw Material 3 widgets

## Running Tests

```bash
# Unit tests (fast, JVM-only, includes repository fake tests)
./gradlew test

# Instrumented tests (requires connected emulator/device)
./gradlew connectedAndroidTest

# Build debug APK only (quick smoke check)
./gradlew assembleDebug
```

All three must pass before a PR is merged.

## Branch & PR Conventions

- Feature branches: `feature/<ticket>-<short-description>`
- Bug fixes: `fix/<ticket>-<short-description>`
- CI must pass (`assembleDebug` + unit tests) before merge
