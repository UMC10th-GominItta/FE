# 고민이따

팀 공용 안드로이드 레포 기반(scaffold) — Kotlin + Jetpack Compose.

## 기술 스택

| 레이어 | 선택 |
|---|---|
| 언어 | Kotlin 2.2.10 |
| UI | Jetpack Compose (Material 3) |
| DI | Hilt 2.59.2 |
| 네비게이션 | Navigation-Compose 2.9.0 |
| 빌드 | AGP 9.1.1 + Gradle 9.4.1 |
| 애노테이션 처리 | KSP 2.2.10-2.0.2 |
| SDK | compileSdk/targetSdk 37, minSdk 26 |

> ⚠️ AGP 는 **9.1.x 를 넘기지 마세요.** Android Studio Panda 2 의 상한이 AGP 9.1 이라, 넘기면 Panda 2 에서 Sync 가 안 됩니다(Panda 4 는 9.2 까지 가능).

---

## 폴더 구조

```
app/src/main/java/com/gominitta/android/
├── GominittaApplication.kt    # @HiltAndroidApp — Hilt 진입점
├── MainActivity.kt             # @AndroidEntryPoint — 단일 액티비티 셸; GominittaTheme + AppNavHost 호스팅
├── di/                         # Hilt 모듈 (AppModule.kt 가 인터페이스 → 구현체 바인딩)
├── data/
│   └── repository/             # Repository 인터페이스 + Fake/Mock 구현
├── navigation/
│   ├── Routes.kt               # 모든 라우트 문자열이 여기에 모임 (얇은 라우트 API)
│   └── AppNavHost.kt           # NavHost — NavController 를 소유하는 유일한 파일
├── ui/
│   ├── theme/
│   │   ├── Color.kt            # 디자인 토큰: 색상 (Figma 값 반영됨)
│   │   ├── Type.kt             # 디자인 토큰: 타이포그래피 스케일
│   │   ├── Spacing.kt          # 디자인 토큰: 4dp 그리드 스페이싱 스케일
│   │   ├── Shape.kt            # 디자인 토큰: 코너 라운드 스케일
│   │   └── Theme.kt            # GominittaTheme — 모든 토큰을 MaterialTheme 에 연결
│   └── components/             # 공용 Composable 컴포넌트 (Gominitta* 래퍼)
└── feature/
    ├── home/                   # 화면 하나당 패키지 하나
    │   ├── HomeScreen.kt
    │   └── HomeViewModel.kt
    └── detail/
        └── DetailScreen.kt
```

---

## 아키텍처 개요

런타임에 레이어가 어떻게 연결되는지:

```
GominittaApplication (@HiltAndroidApp)
  └── MainActivity (@AndroidEntryPoint)
        └── GominittaTheme            ← 모든 하위 composable 에 디자인 토큰 적용
              └── AppNavHost           ← NavController 소유; Routes.* → 화면 매핑
                    ├── HomeScreen     ← onNavigateToDetail: () -> Unit 람다 전달받음
                    │     └── HomeViewModel (@HiltViewModel)
                    │           └── SampleRepository (인터페이스)
                    │                 └── FakeSampleRepository  ← AppModule 의 @Binds
                    └── DetailScreen   ← onNavigateBack: () -> Unit 람다 전달받음
```

지켜야 할 핵심 불변식:
- **`AppNavHost.kt` 만 `NavController` 참조를 가진다** — 화면은 절대 import 하지 않음.
- **ViewModel 은 인터페이스를 주입받는다**, 구체 클래스가 아니라.
- **모든 composable 은 시각 속성을 `MaterialTheme.*` 로 접근한다** — 하드코딩 금지.

---

## 네이밍 컨벤션

### 화면 / 기능(Feature)
- 각 화면은 `feature/<screenName>/` 아래에 위치
- 화면 composable: `<ScreenName>Screen.kt` (예: `HomeScreen.kt`)
- ViewModel(필요 시): `<ScreenName>ViewModel.kt`
- `Routes.kt` 의 라우트 상수: `const val HOME = "home"` (소문자, 공백 없음)

### 네비게이션 라우트
- 단순형: `"home"`, `"settings"`, `"profile"`
- 인자 포함: `Routes.kt` 에 두 항목을 정의 — 등록용 템플릿과
  네비게이션 호출용 헬퍼 함수:
  ```kotlin
  // Routes.kt
  const val ITEM_DETAIL = "item_detail/{itemId}"          // composable() 에서 사용
  fun itemDetailRoute(id: String) = "item_detail/$id"     // navigate() 에서 사용
  ```
  그리고 `AppNavHost.kt` 에서:
  ```kotlin
  composable(
      route = Routes.ITEM_DETAIL,
      arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
  ) { backStackEntry ->
      val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
      ItemDetailScreen(itemId = itemId, onNavigateBack = { navController.popBackStack() })
  }
  ```
- **`Routes.kt` 바깥에서 라우트 문자열을 하드코딩하지 말 것.**

### Repository 레이어
- 인터페이스: `data/repository/` 의 `XxxRepository`
- Fake 구현: `FakeXxxRepository` (DI 기본값)
- 실제 구현(추후): `RemoteXxxRepository` 또는 `LocalXxxRepository`

---

## 디자인 시스템

**색상 토큰은 Figma("고민이따" 파일, "디자인 1차 작업" 페이지의 컬러 가이드)에서
추출한 실제 값이 반영되어 있습니다.** 5개 네임드 램프(`Primary_IB`/`BR`,
`Secondary_YW`/`OG`, `Text_Gray`)를 `Color.kt` 에 정확한 hex 상수로 두고,
Material 시맨틱 역할을 그 램프에서 파생합니다. 따뜻한 크림/베이지 라이트 테마이며,
디자인에 다크 모드가 없어 **라이트 전용**입니다(다크 스킴은 파생 편의용).

폰트는 **Pretendard** 를 번들링해 적용했습니다(`res/font/`, Regular/Medium/SemiBold/Bold,
OFL 라이선스는 `licenses/Pretendard-OFL.txt`). `Type.kt` 의 모든 스타일이 이 패밀리를 사용합니다.
타이포그래피·셰이프·스페이싱의 *수치*는 Figma 시안과 부합하는 Material 스케일을 그대로
유지합니다(뷰어 전용 추출이라 정밀 수치는 근사).

### 토큰 파일

| 파일 | 토큰 카테고리 | Compose 에서 접근 방법 |
|---|---|---|
| `ui/theme/Color.kt` | 색상 토큰 (`PrimaryDefault`, `SurfaceVariant` …) + 팔레트 램프 | `MaterialTheme.colorScheme.*` |
| `ui/theme/Type.kt` | 타이포그래피 스케일 (Material 3: Display/Headline/Body/Label) | `MaterialTheme.typography.*` |
| `ui/theme/Spacing.kt` | 스페이싱 스케일 (4dp 그리드: `xxs` → `xxxl`) | `MaterialTheme.spacing.*` |
| `ui/theme/Shape.kt` | 셰이프 스케일 (ExtraSmall → ExtraLarge 코너) | `MaterialTheme.shapes.*` |
| `ui/theme/Theme.kt` | `GominittaTheme {}` — 모든 토큰을 MaterialTheme 에 연결 | 모든 화면 루트를 감쌈 |

composable 안에서 토큰 사용 예시:
```kotlin
// 스페이싱
MaterialTheme.spacing.md          // 16.dp
MaterialTheme.spacing.lg          // 24.dp

// 색상
MaterialTheme.colorScheme.primary
MaterialTheme.colorScheme.onSurfaceVariant

// 타이포그래피
MaterialTheme.typography.headlineMedium
MaterialTheme.typography.bodySmall

// 셰이프
MaterialTheme.shapes.medium       // 12.dp 코너 (기본 카드)
MaterialTheme.shapes.extraSmall   // 4.dp 코너 (칩, 텍스트 필드)
```

토큰을 갱신할 때는 `Color.kt` / `Type.kt` / `Spacing.kt` / `Shape.kt` 의 **값**만
바꾸세요. 토큰 **이름**은 고정이어야 기능 코드가 영향을 받지 않습니다.

### 컴포넌트 계층

공용 UI 컴포넌트는 `ui/components/` 에 두고 Material 3 프리미티브를 감쌉니다.
그래야 전역 디자인 변경(토큰 갱신)이 기능 코드를 건드리지 않고 전파됩니다.

```
ui/components/
├── GominittaButton.kt   ← Material3 Button 래핑  (labelLarge 타입, primary 색, small 셰이프)
└── GominittaCard.kt     ← Material3 ElevatedCard 래핑 (medium 셰이프, surface 색, md 패딩)
```

**새 컴포넌트 추가 규칙:**
1. 이름 패턴: `Gominitta<Component>.kt` (예: `GominittaTextField.kt`)
2. `modifier: Modifier = Modifier` 를 content 직전 마지막 파라미터로 받는다.
3. 모든 시각 결정을 `MaterialTheme.*` 토큰으로 처리 — 색·크기 하드코딩 금지.
4. `@Preview` 를 라이트 + 다크 + 상태(disabled, error…)별로 포함한다.
5. 기능 화면은 `ui/components/` 에서 import 한다; Material 3 위젯을 직접 호출하지 않는다.

---

## 의존성 관리

모든 버전은 **`gradle/libs.versions.toml`**(Gradle 버전 카탈로그)에 모여 있습니다. 규칙:

1. 새 의존성은 먼저 `libs.versions.toml` 에 추가한다.
2. `build.gradle.kts` 에서 `libs.*` 별칭으로 참조 — 문자열 리터럴 금지.
3. 버전 번호를 중복 작성하지 않는다. `version.ref` 로 `[versions]` 테이블을 가리킨다.

---

## 시작하기

```bash
# 클론
git clone <repo-url>
cd gominitta-android

# 빌드 (최초 실행 시 의존성 다운로드 — 인터넷 또는 로컬 Maven 캐시 필요)
./gradlew assembleDebug

# 연결된 기기 / 에뮬레이터에서 실행
./gradlew installDebug
```

> 참고: CLI 에서 빌드할 때 기본 셸 JDK 가 너무 낮으면(JVM 8) Gradle 9.x 가 실패합니다.
> Android Studio 내장 JBR(JDK 21)을 쓰세요:
> `export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"` (경로는 환경에 맞게).
> Android Studio 에서 열면 자동으로 처리됩니다.

---

## 범위 밖 (Seam 뒤로 미뤄둔 항목)

| 항목 | Seam 위치 |
|---|---|
| 네트워킹 (Retrofit / Ktor) | `SampleRepository` 인터페이스 |
| 로컬 DB (Room / DataStore) | `SampleRepository` 인터페이스 |
| 정밀 타이포·셰이프·스페이싱 수치 | `Type.kt`, `Shape.kt`, `Spacing.kt` |
| 멀티 모듈 분리 | 단일 `:app` + feature 패키지 |
| 레퍼런스 기능 화면 | `feature/home/HomeScreen.kt` 플레이스홀더 |

기존 Seam 뒤에 구현만 끼워 넣으면 되고, 호출부는 바뀌지 않습니다.
