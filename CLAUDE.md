# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android app for the NASA Space Apps Challenge 2026, challenge "Create Health Monitoring Software for Astronauts on Space Missions" (team SpaceDrifters, 48 h hackathon, several people working in parallel). It is a prototype: not a medical device, all crew data is simulated. Respond to the user in Spanish (castellano). `README.md` (in Spanish) is the team-facing guide — keep it in sync when structure or conventions change.

Current state: shared skeleton (Gradle, theme, navigation, placeholder screens) plus the neurocognitive module in progress (steps 1–5 of its plan done): `data/local/AppDatabase` with the cognitive entities in `DatosCognitivos.kt` (no migrations: bump `version`, destructive fallback); Stroop and N-Back tests (instructions → practice only the first time → full-screen test → result phrase vs baseline); `data/repository/CognitivoRepository` saves session + trials + summary in one transaction, computes metrics/state (Java, `domain/cognitivo`) and recalculates baselines. Minimal crew + daily log in `data/local/DatosTripulacion.kt` (`Astronauta`, `RegistroDiario` with sleep hours, KSS sleepiness, symptoms). Sample data: `assets/datos_ejemplo.json` describes each crew member (baseline, per-day deviation in SDs, sleep, symptoms); on first launch (empty DB) `data/ejemplo/CargadorDatosEjemplo` generates the trials (`domain/cognitivo/ejemplo`, Java) and saves them through `CognitivoRepository`, so summaries/states derive from trials. Mission day 14 = install day; to regenerate, clear the app's data. `DatosEjemploTest` pins the demo cases (Maya N-Back worsening + last session still in transit, Leo Stroop ALERTA today + headache, Ane/Ilan normal). Not done yet: doctor's "Pruebas cognitivas" tab, cognitive rules/alerts, "Mi rendimiento" charts. The astronaut using the app is `AppContainer.astronautaActualId` (1 = Maya Okafor, provisional).
- Mission config (Earth delay, Vigilar/Alerta thresholds in SDs, min baseline sessions) lives in `assets/config_mision.json` (read by `data/config/ConfigMision`), not in code.

## Commands

Gradle wrapper (`./gradlew` in Bash, `.\gradlew` / `gradlew.bat` on Windows):

- Build: `./gradlew assembleDebug` · install on device/emulator: `./gradlew installDebug`
- Minimum check before pushing (per README): `./gradlew assembleDebug testDebugUnitTest`
- Unit tests (JVM, `app/src/test`): `./gradlew testDebugUnitTest`
- Single test: `./gradlew testDebugUnitTest --tests "com.example.nasa_spaceappchallenge2026_spacedrifters.ExampleUnitTest.suma_esCorrecta"`
- Instrumented tests (`app/src/androidTest`, needs device/emulator): `./gradlew connectedDebugAndroidTest`
- Lint: `./gradlew lintDebug`

## Build setup (non-obvious bits)

- AGP 9.2.1 with **built-in Kotlin**: do not apply `org.jetbrains.kotlin.android`. The Kotlin version is set by the `kotlin` entry in `gradle/libs.versions.toml` (compose + serialization compiler plugins).
- Gradle runs on JDK 21 (`gradle/gradle-daemon-jvm.properties`); bytecode targets Java 17.
- `compileSdk` 37 is required by Compose 1.12 / Lifecycle 2.11; `targetSdk` 36, `minSdk` 26 (java.time without desugaring).
- Room uses KSP (`ksp(libs.androidx.room.compiler)`), not kapt.
- All versions live in `gradle/libs.versions.toml`; repositories only in `settings.gradle.kts` (`FAIL_ON_PROJECT_REPOS`).
- `.idea/` is git-ignored on purpose (machine-specific).

## Architecture

Package root `com.example.nasa_spaceappchallenge2026_spacedrifters`:

- Single Activity (`MainActivity`) → `SpaceDriftersTheme` → `ui/navigation/AppNavHost`.
- Navigation Compose with **type-safe routes** (`@Serializable` objects/data classes in `ui/navigation/Rutas.kt`). Screens never receive the `NavController`; they get lambdas (`onAtras`, `onMedico`, ...) wired in `AppNavHost`.
- MVVM (ViewModel + StateFlow) with **manual DI**: `SpaceDriftersApp` owns an `di/AppContainer` (create DB/repositories there with `by lazy`); every ViewModel is registered as an `initializer { ... }` in `di/AppViewModelProvider.Factory`, using `CreationExtras.app()` to reach the container. No Hilt. A ViewModel lives next to its screen (`ui/screens/<zona>/XViewModel.kt`); the screen gets it with `viewModel<X>(factory = AppViewModelProvider.Factory)` and reads state via `collectAsStateWithLifecycle()`. Route arguments reach a ViewModel through `createSavedStateHandle().toRoute<Ruta>()` inside the initializer.
- Layers: `data/local` (Room entities/DAOs/AppDatabase) → `data/repository` → `ui/screens/<rol|astronauta|medico>`. Pure logic without Android (e.g. the planned JSON-driven rule engine) goes in `domain/` so it can be unit-tested on the JVM. `domain/cognitivo` is written in **Java** (team member's preference; UI and Room stay Kotlin): no coroutines there — test controllers (e.g. `ControladorStroop`) get timing through the `Temporizador` interface (ViewModel implements it with `viewModelScope` + `delay`; JVM tests use `TemporizadorFalso`) and report phases through a callback. Avoid Java records/sealed types; use final classes.
- Cognitive tests timing: stimulus onset = `withFrameNanos` frame time, response = pointer event `uptimeMillis` — both CLOCK_MONOTONIC so they can be subtracted (don't mix in `elapsedRealtimeNanos`). Test screens share `ui/components/PantallasPrueba.kt` (`ZonaRespuesta` records the touch instant, `InterrumpirAlSalir`, `MensajePrueba`) and apply `ModoPrueba` (full screen + keep screen on) from the instructions screen on, so Android's one-time "Viewing full screen" hint doesn't cover the timed test; nav routes have no transitions.
- Every screen wraps its content in `ui/components/PantallaBase`, which provides the top bar (back button when `onAtras != null`) and the mandatory "Prototipo, no es un dispositivo médico · Datos simulados" footer (`AvisoPrototipo`). Unfinished screens use `EnConstruccion(padding)` from the same file as placeholder content.
- Saving sessions: ViewModels save through `guardarEnSegundoPlano(AppContainer.ambitoAplicacion)` so a save (e.g. an interrupted test, `valida = false`) finishes even if the screen closes. Practice and interrupted sessions get no `ResumenSesion` and never count.
- Tests: instrumented UI tests (`androidTest/NavegacionTest.kt`) use `androidx.compose.ui.test.junit4.v2.createAndroidComposeRule<MainActivity>()` and look up nodes via `activity.getString(R.string.…)`, never hard-coded text, so they pass in either locale. Flow tests that touch Room add `@get:Rule(order = 0) val limpia = BaseDeDatosLimpia()` before the compose rule (`order = 1`) so they don't depend on each other. `StroopCompletoTest` runs a whole Stroop session (~1 min).
- Empty folders hold a `.gitkeep`; delete it when adding the first real file there.

## Conventions

- Code identifiers and UI text are in Spanish. All visible text goes in `res/values/strings.xml` (Spanish, default) **and** `res/values-en/strings.xml` (English, for the NASA submission).
- Fonts are bundled in `res/font` (works offline): Space Grotesk for display/headline/title, Inter for body/labels, JetBrains Mono for `labelMedium` (numbers). Set in `ui/theme/Type.kt` for every M3 style; use `MaterialTheme.typography`, not ad-hoc families. OFL license texts live in `assets/licencias/` and must ship with the fonts.
- App icon = adaptive vector (`drawable/ic_launcher_background|foreground|monochrome.xml`, no PNG mipmaps since minSdk 26); `drawable/ic_logo.xml` is the same logo cropped for in-app use. The Android 12+ splash (`values-v31/themes.xml`) shows that icon on `@color/fondo`. Non-Compose colors (`res/values/colors.xml`) mirror `Color.kt`.
- `PantallaBase(titulo = null)` hides the top bar (for screens with their own header, like the role selector); the prototype footer stays.
- Always-dark "mission control" theme; palette only in `ui/theme/Color.kt`. Use `MaterialTheme.colorScheme` and, for health states, `LocalEstadoColores`. States (Normal / Vigilar / Alerta / Sin datos) must never be conveyed by color alone — always with text; missing data is "Sin datos", never shown as fine.
- Touch targets ≥ 48 dp; every icon has a `contentDescription` from strings. Icons are local vector drawables in `res/drawable/ic_*.xml` (no material-icons dependency).
